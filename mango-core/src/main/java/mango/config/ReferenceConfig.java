package mango.config;

import mango.common.URL;
import mango.common.URLParam;
import mango.core.extension.ExtensionLoader;
import mango.proxy.ProxyFactory;
import mango.proxy.ReferenceInvocationHandler;
import mango.rpc.ConfigHandler;
import mango.rpc.Invoker;
import mango.util.Constants;
import mango.util.NetUtils;
import mango.util.StringUtils;

import java.net.InetAddress;
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
    private Invoker<T> invoker;

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
        initialized = true;
        if (interfaceName == null || interfaceName.length() == 0) {
            throw new IllegalStateException("<mango:reference interface=\"\" /> interface not allow null!");
        }
        try {
            interfaceClass = (Class<T>) Class.forName(interfaceName, true, Thread.currentThread()
                    .getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("reference class not found", e);
        }

        this.invoker = createInvoker();

        proxy = createProxy(invoker);
    }

    private Invoker<T> createInvoker() {
        if(!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("<mango:reference interface=\"\" /> is not interface!");
        }

        List<URL> registryUrls = loadRegistryUrls();
        if (registryUrls == null || registryUrls.size() == 0) {
            throw new IllegalStateException("Should set registry config for reference:" + interfaceClass.getName());
        }

        URL regUrl = registryUrls.get(0);

        ProtocolConfig protocolConfig = protocols.get(0);
        Integer port = protocolConfig.getPort();
        if(port==null) {
            port = Constants.DEFAULT_PORT;
        }
        InetAddress localAddress = NetUtils.getLocalAddress();

        Map<String, String> map = new HashMap<>();
        map.put(URLParam.application.getName(), StringUtils.isNotEmpty(application.getName()) ? application.getName() : URLParam.application.getValue());
        map.put(URLParam.registryProtocol.getName(), regUrl.getProtocol());
        map.put(URLParam.registryAddress.getName(), regUrl.getParameter(URLParam.registryAddress.getName()));
        map.put(URLParam.serialization.getName(), StringUtils.isNotEmpty(protocolConfig.getSerialization()) ? protocolConfig.getSerialization(): URLParam.serialization.getValue());
        map.put(URLParam.version.getName(), StringUtils.isNotEmpty(version) ? version : URLParam.version.getValue());
        map.put(URLParam.group.getName(), StringUtils.isNotEmpty(group) ? group : URLParam.group.getValue());
        map.put(URLParam.side.getName(), Constants.CONSUMER);
        map.put(URLParam.requestTimeout.getName(), String.valueOf(getTimeout()));
        map.put(URLParam.timestamp.getName(), String.valueOf(System.currentTimeMillis()));

        URL refUrl = new URL(URLParam.codec.getValue(), localAddress.getHostAddress(), port, interfaceClass.getName(), map);

        ConfigHandler configHandler = ExtensionLoader.getExtensionLoader(ConfigHandler.class).getExtension(Constants.DEFAULT_VALUE);
        invoker = configHandler.refer(interfaceClass, regUrl, refUrl);
        return invoker;
    }

    private T createProxy(Invoker<T> invoker) {
        String proxyType = invoker.getServiceUrl().getParameter(URLParam.proxyType.getName(), URLParam.proxyType.getValue());
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getExtension(proxyType);
        return (T) proxyFactory.getProxy(interfaceClass, new ReferenceInvocationHandler<>(invoker));
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
        proxy = null;
        if(invoker!=null) {
            invoker.destroy();
        }
        invoker = null;
    }
}
