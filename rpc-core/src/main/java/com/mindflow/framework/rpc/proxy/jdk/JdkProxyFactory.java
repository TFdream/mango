package com.mindflow.framework.rpc.proxy.jdk;

import com.mindflow.framework.rpc.proxy.ProxyFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class JdkProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Class<T> clz, InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clz}, invocationHandler);
    }
}
