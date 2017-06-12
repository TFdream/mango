package com.mindflow.framework.rpc.client;

import com.mindflow.framework.registry.DiscoveryService;
import com.mindflow.framework.rpc.DefaultRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class RpcClient {
    private AtomicLong idGenerator = new AtomicLong(0);

    private DiscoveryService discoveryService;

    public RpcClient(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    public <T> T create(Class<T> interfaceClass) {
        if(!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("");
        }

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                DefaultRequest request = new DefaultRequest();
                request.setRequestId(idGenerator.getAndIncrement());
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setArguments(args);

                return null;
            }
        });
    }
}
