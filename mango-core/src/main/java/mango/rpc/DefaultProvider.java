package mango.rpc;

import mango.common.URL;
import mango.core.DefaultResponse;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcFrameworkException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Ricky Fung
 */
public class DefaultProvider<T> extends AbstractProvider<T> {
    protected T proxyImpl;

    public DefaultProvider(T proxyImpl, URL url, Class<T> clz) {
        super(url, clz);
        this.proxyImpl = proxyImpl;
    }

    @Override
    public Class<T> getInterface() {
        return clz;
    }

    @Override
    public Response invoke(Request request) {

        DefaultResponse response = new DefaultResponse();
        response.setRequestId(request.getRequestId());

        long processStartTime = System.currentTimeMillis();

        Method method = lookup(request);

        if (method == null) {
            RpcFrameworkException exception =
                    new RpcFrameworkException("Service method not exist: " + request.getInterfaceName() + "." + request.getMethodName());

            response.setException(exception);
            return response;
        }
        try {
            Object result = method.invoke(proxyImpl, request.getArguments());
            response.setResult(result);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        response.setProcessTime(System.currentTimeMillis() - processStartTime);
        return response;
    }
}
