package mango.protocol;

import mango.cluster.loadbalance.LoadBalance;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.Request;
import mango.core.Response;
import mango.core.extension.ExtensionLoader;
import mango.exception.RpcFrameworkException;
import mango.registry.NotifyListener;
import mango.registry.Registry;
import mango.registry.RegistryFactory;
import mango.rpc.*;
import mango.transport.NettyClient;
import mango.transport.NettyClientImpl;
import mango.transport.NettyServer;
import mango.transport.NettyServerImpl;
import mango.util.MangoFrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class DefaultRpcProtocol implements Protocol {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ConcurrentHashMap<String, Exporter<?>> exporterMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, NettyServer> ipPort2Server = new ConcurrentHashMap<>();
    // 多个service可能在相同端口进行服务暴露，因此来自同个端口的请求需要进行路由以找到相应的服务，同时不在该端口暴露的服务不应该被找到
    private Map<String, MessageRouter> ipPort2RequestRouter = new HashMap<String, MessageRouter>();

    @Override
    public <T> Invoker<T> refer(Class<T> clz, URL url, URL serviceUrl) {
        if (url == null) {
            throw new RpcFrameworkException(this.getClass().getSimpleName() + " refer Error: url is null");
        }
        if (clz == null) {
            throw new RpcFrameworkException(this.getClass().getSimpleName() + " refer Error: class is null, url=" + url);
        }
        Invoker<T> invoker = createInvoker(clz, url, serviceUrl);
        invoker.init();

        logger.info(this.getClass().getSimpleName() + " refer Success: url=" + url);
        return invoker;
    }

    @Override
    public <T> Exporter<T> export(Provider<T> provider, URL url) {
        if (url == null) {
            throw new RpcFrameworkException(this.getClass().getSimpleName() + " export Error: url is null");
        }

        if (provider == null) {
            throw new RpcFrameworkException(this.getClass().getSimpleName() + " export Error: provider is null, url=" + url);
        }

        String protocolKey = MangoFrameworkUtils.getProtocolKey(url);

        synchronized (exporterMap) {
            Exporter<T> exporter = (Exporter<T>) exporterMap.get(protocolKey);

            if (exporter != null) {
                throw new RpcFrameworkException(this.getClass().getSimpleName() + " export Error: service already exist, url=" + url);
            }

            exporter = createExporter(provider, url);
            exporter.init();
            exporterMap.put(protocolKey, exporter);
            logger.info(this.getClass().getSimpleName() + " export success: url=" + url);
            return exporter;
        }
    }

    protected <T> Invoker<T> createInvoker(Class<T> clz, URL url, URL serviceUrl) {
        return new DefaultRpcInvoker<T>(clz, url, serviceUrl);
    }

    protected <T> Exporter<T> createExporter(Provider<T> provider, URL url) {
        return new DefaultRpcExporter<T>(provider, url);
    }

    class DefaultRpcInvoker<T> implements Invoker<T>, NotifyListener {
        protected Class<T> clz;
        private URL url;
        protected URL serviceUrl;

        private NettyClient client;
        private List<URL> urls; //服务提供者地址
        private LoadBalance lb;

        public DefaultRpcInvoker(Class<T> clz, URL url, URL serviceUrl) {
            this.clz = clz;
            this.url = url;
            this.serviceUrl = serviceUrl;

            this.client = new NettyClientImpl(serviceUrl);
        }

        @Override
        public Class<T> getInterface() {
            return clz;
        }

        @Override
        public Response call(Request request) {
            try {
                //load-balance
                URL url = lb.select(urls);
                String address = String.format("%s:%d", url.getHost(), url.getPort());
                logger.info("LB select: {}", address);
                long timeout = url.getIntParameter(URLParam.requestTimeout.getName(), URLParam.requestTimeout.getIntValue());
                Response resp = client.invokeSync(address, request, timeout);
                return resp;
            } catch (Exception e) {
                throw new RpcFrameworkException("invoke exception", e);
            }
        }

        @Override
        public URL getServiceUrl() {
            return serviceUrl;
        }

        @Override
        public void init() {
            String loadbalance = serviceUrl.getParameter(URLParam.loadBalance.getName(), URLParam.loadBalance.getValue());
            lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(loadbalance);

            RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(url.getProtocol());
            Registry registry;
            try {
                registry = registryFactory.getRegistry(url);
            }catch (Exception e) {
                throw new RpcFrameworkException("get registry from ["+url.getProtocol()+":"+url.getHost()+"] error", e);
            }
            try {
                this.urls = registry.discover(serviceUrl);
                if(this.urls==null || this.urls.isEmpty()) {
                    throw new IllegalStateException("no provider for url:"+url);
                }

                //订阅服务
                registry.subscribe(serviceUrl, this);
            } catch (Exception e) {
                throw new RpcFrameworkException("Unable discover/subscribe service:"+url.getPath() + " from ["+url.getProtocol()+":"+url.getHost()+"]", e);
            }
            client.start();
        }

        @Override
        public void destroy() {
            client.shutdown();
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public String desc() {
            return null;
        }

        @Override
        public URL getUrl() {
            return url;
        }

        @Override
        public void notify(URL registryUrl, List<URL> urls) {
            logger.info("client notify from %s", registryUrl);
        }
    }

    class DefaultRpcExporter<T> implements Exporter<T> {
        protected Provider<T> provider;
        protected URL url;
        private NettyServer server;
        public DefaultRpcExporter(Provider<T> provider, URL url) {
            this.url = url;
            this.provider = provider;
            this.server = initEnv(url);
        }

        private NettyServer initEnv(URL url) {
            String ipPort = url.getServerAndPort();

            MessageRouter router = initRequestRouter(url);

            NettyServer server = null;
            synchronized (ipPort2Server) {
                server = ipPort2Server.get(ipPort);
                if (server == null) {
                    server = new NettyServerImpl(url, router);
                    ipPort2Server.put(ipPort, server);
                }
            }
            return server;
        }

        private MessageRouter initRequestRouter(URL url) {
            MessageRouter requestRouter = null;
            String ipPort = url.getServerAndPort();

            synchronized (ipPort2RequestRouter) {
                requestRouter = ipPort2RequestRouter.get(ipPort);

                if (requestRouter == null) {
                    requestRouter = new MessageRouter(provider);
                    ipPort2RequestRouter.put(ipPort, requestRouter);
                } else {
                    requestRouter.addProvider(provider);
                }
            }

            return requestRouter;
        }

        @Override
        public Provider<T> getProvider() {
            return provider;
        }

        @Override
        public void unExport() {
            this.server.shutdown();
        }

        @Override
        public void init() {
            this.server.open();
        }

        @Override
        public void destroy() {
            this.server.shutdown();
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public String desc() {
            return "";
        }

        @Override
        public URL getUrl() {
            return url;
        }
    }
}
