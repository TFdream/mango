package mango.protocol;

import mango.common.URL;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcFrameworkException;
import mango.filter.Filter;
import mango.rpc.Exporter;
import mango.rpc.Protocol;
import mango.rpc.Provider;
import mango.rpc.Reference;
import mango.util.Constants;
import java.util.List;

/**
 * @author Ricky Fung
 */
public class ProtocolFilterWrapper implements Protocol {

    private Protocol protocol;

    public ProtocolFilterWrapper(Protocol protocol) {
        if (protocol == null) {
            throw new RpcFrameworkException("Protocol is null when construct "+this.getClass().getName());
        }
        this.protocol = protocol;
    }

    @Override
    public <T> Reference<T> refer(Class<T> clz, URL url, URL serviceUrl) {
        return buildReferenceChain(protocol.refer(clz, url, serviceUrl), url);
    }

    @Override
    public <T> Exporter<T> export(Provider<T> provider, URL url) {
        return protocol.export(buildProviderChain(provider, url), url);
    }

    @Override
    public void destroy() {
        protocol.destroy();
    }

    private <T> Reference<T> buildReferenceChain(Reference<T> reference, URL url) {
        List<Filter> filters = getFilters(url, Constants.CONSUMER);
        if (filters == null || filters.size() == 0) {
            return reference;
        }
        Reference<T> lastReference = reference;
        for (Filter filter : filters) {
            final Filter f = filter;
            final Reference<T> lr = lastReference;
            lastReference = new Reference<T>() {
                @Override
                public int activeCount() {
                    return lr.activeCount();
                }

                @Override
                public URL getServiceUrl() {
                    return lr.getServiceUrl();
                }

                @Override
                public Class<T> getInterface() {
                    return lr.getInterface();
                }

                @Override
                public Response call(Request request) {
                    return null;
                }

                @Override
                public void init() {
                    lr.init();
                }

                @Override
                public void destroy() {
                    lr.destroy();
                }

                @Override
                public boolean isAvailable() {
                    return lr.isAvailable();
                }

                @Override
                public String desc() {
                    return lr.desc();
                }

                @Override
                public URL getUrl() {
                    return lr.getUrl();
                }
            };
        }
        return lastReference;
    }

    private <T> Provider<T> buildProviderChain(Provider<T> provider, URL url) {
        List<Filter> filters = getFilters(url, Constants.PROVIDER);
        if (filters == null || filters.size() == 0) {
            return provider;
        }
        Provider<T> lastProvider = provider;
        for (Filter filter : filters) {
            final Filter f = filter;
            final Provider<T> lp = lastProvider;
            lastProvider = new Provider<T>() {
                @Override
                public Response call(Request request) {
                    return f.filter(lp, request);
                }

                @Override
                public String desc() {
                    return lp.desc();
                }

                @Override
                public void destroy() {
                    lp.destroy();
                }

                @Override
                public Class<T> getInterface() {
                    return lp.getInterface();
                }

                @Override
                public URL getUrl() {
                    return lp.getUrl();
                }

                @Override
                public void init() {
                    lp.init();
                }

                @Override
                public boolean isAvailable() {
                    return lp.isAvailable();
                }
            };
        }
        return lastProvider;
    }

    private List<Filter> getFilters(URL url, String category) {
        return null;
    }
}
