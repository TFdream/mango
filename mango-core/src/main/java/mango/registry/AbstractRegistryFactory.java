package mango.registry;

import mango.common.URL;
import mango.exception.RpcFrameworkException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    private final ConcurrentHashMap<String, Registry> registries = new ConcurrentHashMap<String, Registry>();
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public Registry getRegistry(URL url) {
        String registryUri = getRegistryUri(url);
        try {
            lock.lock();
            Registry registry = registries.get(registryUri);
            if (registry != null) {
                return registry;
            }
            registry = createRegistry(url);
            if (registry == null) {
                throw new RpcFrameworkException("Create registry false for url:" + url);
            }
            registries.put(registryUri, registry);
            return registry;
        } catch (Exception e) {
            throw new RpcFrameworkException("Create registry false for url:" + url, e);
        } finally {
            lock.unlock();
        }
    }

    protected String getRegistryUri(URL url) {
        String registryUri = url.getUri();
        return registryUri;
    }

    protected abstract Registry createRegistry(URL url);
}
