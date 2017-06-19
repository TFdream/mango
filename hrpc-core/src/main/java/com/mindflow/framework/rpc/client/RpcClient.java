package com.mindflow.framework.rpc.client;

import com.google.common.base.Preconditions;
import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.config.RegistryConfig;
import com.mindflow.framework.rpc.core.extension.ExtensionLoader;
import com.mindflow.framework.rpc.proxy.ClientInvocationHandler;
import com.mindflow.framework.rpc.proxy.ProxyFactory;
import com.mindflow.framework.rpc.proxy.jdk.JdkProxyFactory;
import com.mindflow.framework.rpc.registry.RegistryFactory;
import com.mindflow.framework.rpc.util.Constants;
import com.mindflow.framework.rpc.util.NetUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.InetAddress;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class RpcClient implements InitializingBean, DisposableBean {

    private transient volatile boolean initialized = false;
    private RegistryConfig registryConfig;
    private long timeoutInMillis = 1000;
    private ProxyFactory proxyFactory  = new JdkProxyFactory();
    private RegistryFactory registryFactory;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public <T> T create(Class<T> interfaceClass) {
        if(!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("");
        }

        checkInit();

        InetAddress localAddress = NetUtils.getLocalAddress();
        URL url = new URL(URLParamName.codec.getValue(), localAddress.getHostAddress(), 0, interfaceClass.getName());
        url.addParameter(Constants.REGISTRY_PROTOCOL, this.registryConfig.getProtocol());
        url.addParameter(Constants.REGISTRY_ADDRESS, this.registryConfig.getAddress());
        url.addParameter(Constants.SIDE, "consumer");
        url.addParameter(Constants.TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        ClientInvocationHandler handler = new ClientInvocationHandler(url, registryFactory, timeoutInMillis);
        return proxyFactory.getProxy(interfaceClass, handler);
    }

    private void checkInit() {
        if (!initialized) {
            doInit();
        }
    }

    private void doInit() {
        if (initialized) {
            return;
        }

        Preconditions.checkNotNull(this.registryConfig);
        Preconditions.checkNotNull(this.registryConfig.getProtocol());
        Preconditions.checkNotNull(this.registryConfig.getAddress());

        registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(this.registryConfig.getProtocol());

        initialized = true;
    }

    @Override
    public void destroy() throws Exception {

    }

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }
}
