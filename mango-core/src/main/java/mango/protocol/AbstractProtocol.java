package mango.protocol;

import mango.common.URL;
import mango.exception.RpcFrameworkException;
import mango.rpc.Exporter;
import mango.rpc.Invoker;
import mango.rpc.Protocol;
import mango.rpc.Provider;
import mango.util.MangoFrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public abstract class AbstractProtocol implements Protocol {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ConcurrentHashMap<String, Exporter<?>> exporterMap = new ConcurrentHashMap<>();

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

    protected abstract <T> Invoker<T> createInvoker(Class<T> clz, URL url, URL serviceUrl);

    protected abstract <T> Exporter<T> createExporter(Provider<T> provider, URL url);
}
