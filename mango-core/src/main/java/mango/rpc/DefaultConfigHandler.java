package mango.rpc;

import mango.cluster.Cluster;
import mango.cluster.DefaultCluster;
import mango.cluster.HaStrategy;
import mango.cluster.LoadBalance;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.extension.ExtensionLoader;
import mango.exception.RpcFrameworkException;
import mango.proxy.ProxyFactory;
import mango.proxy.ReferenceInvocationHandler;
import mango.registry.Registry;
import mango.registry.RegistryFactory;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class DefaultConfigHandler implements ConfigHandler {

    @Override
    public <T> Cluster<T> buildCluster(Class<T> interfaceClass, URL refUrl, List<URL> registryUrls) {
        DefaultCluster<T> cluster = new DefaultCluster(interfaceClass, refUrl, registryUrls);
        String loadBalanceName = refUrl.getParameter(URLParam.loadBalance.getName(), URLParam.loadBalance.getValue());
        String haStrategyName = refUrl.getParameter(URLParam.haStrategy.getName(), URLParam.haStrategy.getValue());
        LoadBalance<T> loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(loadBalanceName);
        HaStrategy<T> ha = ExtensionLoader.getExtensionLoader(HaStrategy.class).getExtension(haStrategyName);
        cluster.setLoadBalance(loadBalance);
        cluster.setHaStrategy(ha);

        cluster.init();
        return cluster;
    }

    @Override
    public <T> T refer(Class<T> interfaceClass, List<Cluster<T>> cluster, String proxyType) {
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getExtension(proxyType);
        return (T) proxyFactory.getProxy(interfaceClass, new ReferenceInvocationHandler<>(interfaceClass, cluster));
    }

    @Override
    public <T> Exporter<T> export(Class<T> interfaceClass, T ref, URL serviceUrl, List<URL> registryUrls) {

        String protocolName = serviceUrl.getParameter(URLParam.protocol.getName(), URLParam.protocol.getValue());
        Provider<T> provider = new DefaultProvider<T>(ref, serviceUrl, interfaceClass);
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(protocolName);
        Exporter<T> exporter = protocol.export(provider, serviceUrl);

        // register service
        register(registryUrls, serviceUrl);

        return exporter;
    }

    private void register(List<URL> registryUrls, URL serviceUrl) {

        for (URL registryUrl : registryUrls) {
            // 根据check参数的设置，register失败可能会抛异常，上层应该知晓
            RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(registryUrl.getProtocol());
            if (registryFactory == null) {
                throw new RpcFrameworkException("register error! Could not find extension for registry protocol:" + registryUrl.getProtocol()
                                + ", make sure registry module for " + registryUrl.getProtocol() + " is in classpath!");
            }
            try {
                Registry registry = registryFactory.getRegistry(registryUrl);
                registry.register(serviceUrl);
            } catch (Exception e) {
                throw new RpcFrameworkException("register error! Could not registry service:" + serviceUrl.getPath()
                        + " for " + registryUrl.getProtocol());
            }
        }
    }
}
