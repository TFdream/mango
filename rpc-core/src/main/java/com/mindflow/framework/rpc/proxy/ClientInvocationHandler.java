package com.mindflow.framework.rpc.proxy;

import com.mindflow.framework.rpc.config.NettyClientConfig;
import com.mindflow.framework.rpc.core.DefaultRequest;
import com.mindflow.framework.rpc.core.Response;
import com.mindflow.framework.rpc.exception.RpcFrameworkException;
import com.mindflow.framework.rpc.transport.NettyClient;
import com.mindflow.framework.rpc.transport.NettyClientImpl;
import com.mindflow.framework.rpc.util.Constants;
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
    public ClientInvocationHandler(long timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
        nettyClient = new NettyClientImpl(new NettyClientConfig());
        nettyClient.start();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //toString,equals,hashCode,finalize等接口未声明的方法不进行远程调用
        if(method.getDeclaringClass().equals(Object.class)){
            if ("toString".equals(method.getName())) {
                return "";
            }
            throw new RpcFrameworkException("can not invoke local method:" + method.getName());
        }

        DefaultRequest request = new DefaultRequest();
        request.setRequestId(RequestIdGenerator.getRequestId());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setArguments(args);
        request.setType(Constants.REQUEST_SYNC);

        try {
            Response resp = nettyClient.invokeSync("127.0.0.1:21918", request, timeoutInMillis);
            return getValue(resp);
        } catch (RuntimeException e) {
            throw new RpcFrameworkException("", e);
        }
    }

    public Object getValue(Response resp) {
        Exception exception = resp.getException();
        if (exception != null) {
            throw (exception instanceof RuntimeException) ? (RuntimeException) exception : new RpcFrameworkException(
                    exception.getMessage(), exception);
        }
        return resp.getResult();
    }
}
