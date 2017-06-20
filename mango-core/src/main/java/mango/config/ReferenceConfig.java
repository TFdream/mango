package mango.config;

import mango.common.URL;
import mango.common.URLParam;
import mango.core.extension.ExtensionLoader;
import mango.proxy.ProxyFactory;
import mango.rpc.RpcInvoker;
import mango.util.Constants;
import mango.util.NetUtils;
import mango.util.StringUtils;

import java.net.InetAddress;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ReferenceConfig<T> extends AbstractInterfaceConfig {

    private Class<T> interfaceClass;
    protected transient volatile T proxy;

    private transient volatile boolean initialized;
    private ProxyFactory proxyFactory  = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getDefaultExtension();
    private RpcInvoker invoker;

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

        proxy = createProxy();
    }

    private T createProxy() {
        if(!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("<mango:reference interface=\"\" /> is not interface!");
        }

        RegistryConfig registryConfig = registries.get(0);
        ProtocolConfig protocolConfig = protocols.get(0);

        InetAddress localAddress = NetUtils.getLocalAddress();
        URL url = new URL(URLParam.codec.getValue(), localAddress.getHostAddress(), 0, interfaceClass.getName());
        url.addParameter(URLParam.registryProtocol.getName(), registryConfig.getProtocol());
        url.addParameter(URLParam.registryAddress.getName(), registryConfig.getAddress());
        url.addParameter(URLParam.serialization.getName(), StringUtils.isNotEmpty(protocolConfig.getSerialization()) ? protocolConfig.getSerialization(): URLParam.serialization.getValue());
        url.addParameter(URLParam.version.getName(), StringUtils.isNotEmpty(version) ? version : URLParam.version.getValue());
        url.addParameter(URLParam.group.getName(), StringUtils.isNotEmpty(group) ? group : URLParam.group.getValue());
        url.addParameter(URLParam.side.getName(), Constants.CONSUMER);
        url.addParameter(URLParam.timestamp.getName(), String.valueOf(System.currentTimeMillis()));

        invoker = new RpcInvoker(url, timeout);
        return (T) proxyFactory.getProxy(interfaceClass, invoker);
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
        invoker.close();
    }
}
