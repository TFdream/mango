package mango.protocol;

import mango.common.URL;
import mango.exception.RpcFrameworkException;
import mango.rpc.Exporter;
import mango.rpc.MessageRouter;
import mango.rpc.Provider;
import mango.transport.NettyServer;
import mango.transport.NettyServerImpl;
import mango.util.MangoFrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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

    protected <T> Exporter<T> createExporter(Provider<T> provider, URL url) {
        return new DefaultRpcExporter<T>(provider, url);
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
