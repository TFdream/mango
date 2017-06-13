package com.mindflow.framework.rpc.client;

import com.mindflow.framework.rpc.proxy.ClientInvocationHandler;
import com.mindflow.framework.rpc.proxy.ProxyFactory;
import com.mindflow.framework.rpc.proxy.jdk.JdkProxyFactory;
import com.mindflow.framework.rpc.registry.DiscoveryService;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class RpcClient {
    private transient volatile boolean initialized = false;
    private DiscoveryService discoveryService;
    private long timeoutInMillis = 1000;
    private ProxyFactory proxyFactory  = new JdkProxyFactory();
    private ClientInvocationHandler handler = new ClientInvocationHandler();

    public RpcClient(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    public <T> T create(Class<T> interfaceClass) {
        if(!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("");
        }

        checkInit();

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

        //do...
        try {
            discoveryService.discover(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initialized = true;
    }
}
