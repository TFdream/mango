package com.mindflow.framework.rpc.proxy;

import com.mindflow.framework.rpc.core.DefaultRequest;
import com.mindflow.framework.rpc.core.Response;
import com.mindflow.framework.rpc.transport.NettyClient;
import com.mindflow.framework.rpc.util.RequestIdGenerator;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ClientInvocationHandler implements InvocationHandler {
    private NettyClient nettyClient;
    private long timeoutInMillis;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        DefaultRequest request = new DefaultRequest();
        request.setRequestId(RequestIdGenerator.getRequestId());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setArguments(args);

        try {
            Response resp = nettyClient.invokeSync("", request, timeoutInMillis);

        } catch (Exception e) {

        }
        return null;
    }
}
