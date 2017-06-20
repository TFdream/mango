package com.mindflow.framework.rpc.config;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.proxy.ProxyFactory;
import com.mindflow.framework.rpc.invoker.RpcInvoker;
import com.mindflow.framework.rpc.proxy.jdk.JdkProxyFactory;
import com.mindflow.framework.rpc.util.NetUtils;
import com.mindflow.framework.rpc.util.StringUtils;

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
    private ProxyFactory proxyFactory  = new JdkProxyFactory();
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
            throw new IllegalStateException("<hrpc:reference interface=\"\" /> interface not allow null!");
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
            throw new IllegalArgumentException("<hrpc:reference interface=\"\" /> is not interface!");
        }

        RegistryConfig registryConfig = registries.get(0);
        ProtocolConfig protocolConfig = protocols.get(0);

        InetAddress localAddress = NetUtils.getLocalAddress();
        URL url = new URL(URLParamName.codec.getValue(), localAddress.getHostAddress(), 0, interfaceClass.getName());
        url.addParameter(URLParamName.registryProtocol.getName(), registryConfig.getProtocol());
        url.addParameter(URLParamName.registryAddress.getName(), registryConfig.getAddress());
        url.addParameter(URLParamName.serialization.getName(), StringUtils.isNotEmpty(protocolConfig.getSerialization()) ? protocolConfig.getSerialization(): URLParamName.serialization.getValue());
        url.addParameter(URLParamName.version.getName(), StringUtils.isNotEmpty(version) ? version : URLParamName.version.getValue());
        url.addParameter(URLParamName.group.getName(), StringUtils.isNotEmpty(group) ? group : URLParamName.group.getValue());
        url.addParameter(URLParamName.side.getName(), "consumer");
        url.addParameter(URLParamName.timestamp.getName(), String.valueOf(System.currentTimeMillis()));

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
