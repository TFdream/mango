package mango.rpc;

import mango.common.URL;
import mango.core.DefaultResponse;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcBizException;
import mango.exception.RpcFrameworkException;
import mango.util.MangoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ricky Fung
 */
public class DefaultMessageRouter implements MessageRouter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConcurrentHashMap<String, Provider<?>> providers = new ConcurrentHashMap<String, Provider<?>>();

    @Override
    public Response handle(Request request) {

        String serviceKey = MangoUtils.getServiceKey(request);

        Provider<?> provider = providers.get(serviceKey);

        if (provider == null) {
            logger.error(this.getClass().getSimpleName() + " handler Error: provider not exist serviceKey=" + serviceKey);
            RpcFrameworkException exception =
                    new RpcFrameworkException(this.getClass().getSimpleName() + " handler Error: provider not exist serviceKey="
                            + serviceKey );

            DefaultResponse response = new DefaultResponse();
            response.setException(exception);
            return response;
        }

        return call(request, provider);
    }

    protected Response call(Request request, Provider<?> provider) {
        try {
            return provider.call(request);
        } catch (Exception e) {
            DefaultResponse response = new DefaultResponse();
            response.setException(new RpcBizException("provider call process error", e));
            return response;
        }
    }

    @Override
    public <T> Exporter<T> register(Provider<T> provider, URL url) {
        addProvider(provider);
        return new DefaultRpcExporter<T>(provider, url);
    }

    class DefaultRpcExporter<T> implements Exporter<T> {
        protected Provider<T> provider;
        protected URL url;

        public DefaultRpcExporter(Provider<T> provider, URL url) {
            this.url = url;
            this.provider = provider;
        }

        @Override
        public URL getUrl() {
            return url;
        }

        @Override
        public Provider<T> getProvider() {
            return provider;
        }

        @Override
        public void close() {

        }

    }

    public synchronized void addProvider(Provider<?> provider) {
        String serviceKey = MangoUtils.getServiceKey(provider.getUrl());
        if (providers.containsKey(serviceKey)) {
            throw new RpcFrameworkException("provider already exist: " + serviceKey);
        }
        providers.put(serviceKey, provider);
   }

    public synchronized void removeProvider(Provider<?> provider) {
        String serviceKey = MangoUtils.getServiceKey(provider.getUrl());
        providers.remove(serviceKey);
    }

}
