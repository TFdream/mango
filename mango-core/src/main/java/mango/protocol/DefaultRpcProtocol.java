package mango.protocol;

import mango.common.URL;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcFrameworkException;
import mango.rpc.Exporter;
import mango.rpc.MessageRouter;
import mango.rpc.Provider;
import mango.rpc.Reference;
import mango.transport.NettyClient;
import mango.transport.NettyClientImpl;
import mango.transport.NettyServer;
import mango.transport.NettyServerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class DefaultRpcProtocol extends AbstractProtocol {

    private ConcurrentHashMap<String, NettyServer> ipPort2Server = new ConcurrentHashMap<>();
    // 多个service可能在相同端口进行服务暴露，因此来自同个端口的请求需要进行路由以找到相应的服务，同时不在该端口暴露的服务不应该被找到
    private Map<String, MessageRouter> ipPort2RequestRouter = new HashMap<String, MessageRouter>();

    @Override
    protected <T> Reference<T> createReference(Class<T> clz, URL url, URL serviceUrl) {
        return new DefaultRpcReference<T>(clz, url, serviceUrl);
    }

    @Override
    protected <T> Exporter<T> createExporter(Provider<T> provider, URL url) {
        return new DefaultRpcExporter<T>(provider, url);
    }

    class DefaultRpcReference<T> implements Reference<T> {
        protected Class<T> clz;
        private URL url;
        protected URL serviceUrl;
        private NettyClient client;

        public DefaultRpcReference(Class<T> clz, URL url, URL serviceUrl) {
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
                Response resp = client.invokeSync(request);
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
            this.client.open();
        }

        @Override
        public void destroy() {
            try{
                client.close();
            } catch (Exception e){
                logger.error("reference destroy error", e);
            }
        }

        @Override
        public boolean isAvailable() {
            return client.isAvailable();
        }

        @Override
        public String desc() {
            return null;
        }

        @Override
        public URL getUrl() {
            return url;
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

        }

        @Override
        public void init() {
            this.server.open();
        }

        @Override
        public void destroy() {
            this.server.close();
        }

        @Override
        public boolean isAvailable() {
            return this.server.isAvailable();
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
