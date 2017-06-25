package mango.config;

import mango.cluster.Cluster;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.extension.ExtensionLoader;
import mango.rpc.ConfigHandler;
import mango.util.Constants;
import mango.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ReferenceConfig<T> extends AbstractInterfaceConfig {

    private static final long serialVersionUID = 3259358868568571457L;
    private Class<T> interfaceClass;
    protected transient volatile T proxy;

    private transient volatile boolean initialized;
    private List<Cluster<T>> clusters;

    public T get() {
        if (proxy == null) {
            init();
        }
        return proxy;
    }

    private synchronized void init() {
        if (initialized) {
            return;
        }

        if (interfaceName == null || interfaceName.length() == 0) {
            throw new IllegalStateException("<mango:reference interface=\"\" /> interface not allow null!");
        }
        try {
            interfaceClass = (Class<T>) Class.forName(interfaceName, true, Thread.currentThread()
                    .getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("reference class not found", e);
        }

        initProxy();

        initialized = true;
    }

    private void initProxy() {
        if(!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("<mango:reference interface=\"\" /> is not interface!");
        }

        List<URL> registryUrls = loadRegistryUrls();
        if (registryUrls == null || registryUrls.size() == 0) {
            throw new IllegalStateException("Should set registry config for reference:" + interfaceClass.getName());
        }

        ConfigHandler configHandler = ExtensionLoader.getExtensionLoader(ConfigHandler.class).getExtension(Constants.DEFAULT_VALUE);

        clusters = new ArrayList<>(protocols.size());
        String proxyType = null;
        for(ProtocolConfig protocol : protocols) {

            Map<String, String> map = new HashMap<>();
            map.put(URLParam.application.getName(), StringUtils.isNotEmpty(application.getName()) ? application.getName() : URLParam.application.getValue());
            map.put(URLParam.serialization.getName(), StringUtils.isNotEmpty(protocol.getSerialization()) ? protocol.getSerialization(): URLParam.serialization.getValue());
            map.put(URLParam.version.getName(), StringUtils.isNotEmpty(version) ? version : URLParam.version.getValue());
            map.put(URLParam.group.getName(), StringUtils.isNotEmpty(group) ? group : URLParam.group.getValue());
            map.put(URLParam.side.getName(), Constants.CONSUMER);
            map.put(URLParam.requestTimeout.getName(), String.valueOf(getTimeout()));
            map.put(URLParam.timestamp.getName(), String.valueOf(System.currentTimeMillis()));
            map.put(URLParam.check.getName(), isCheck().toString());

            String hostAddress = getLocalHostAddress(protocol);
            Integer port = getProtocolPort(protocol);

            URL refUrl = new URL(protocol.getName(), hostAddress, port, interfaceClass.getName(), map);

            clusters.add(configHandler.buildCluster(interfaceClass, refUrl, registryUrls));

            proxyType = refUrl.getParameter(URLParam.proxyType.getName(), URLParam.proxyType.getValue());
        }

        this.proxy = configHandler.refer(interfaceClass, clusters, proxyType);
    }

    public T getProxy() {
        return proxy;
    }

    public void setProxy(T proxy) {
        this.proxy = proxy;
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    protected void destroy0() throws Exception {
        if (clusters != null) {
            for (Cluster<T> cluster : clusters) {
                cluster.destroy();
            }
        }
        proxy = null;
    }
}
