package mango.rpc;

import com.google.common.collect.Maps;
import mango.core.DefaultRequest;
import mango.core.DefaultResponse;
import mango.exception.RpcFrameworkException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Ricky Fung
 */
public class MessageHandler {
    private final Map<String, Object> serviceBeanMap = Maps.newConcurrentMap();

    private MessageHandler() {}

    public static MessageHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addServiceBean(String interfaceName, Object object) {
        serviceBeanMap.put(interfaceName, object);
    }

    public DefaultResponse invoke(DefaultRequest request, long processStartTime) {
        DefaultResponse response = new DefaultResponse();
        response.setRequestId(request.getRequestId());

        Object serviceBean = serviceBeanMap.get(request.getClassName());
        if(serviceBean!=null) {
            try {
                Method method = serviceBean.getClass().getDeclaredMethod(request.getMethodName(), request.getParameterTypes());
                Object result = method.invoke(serviceBean, request.getArguments());
                response.setResult(result);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            RpcFrameworkException ex = new RpcFrameworkException("");
            response.setException(ex);
        }
        response.setProcessTime(System.currentTimeMillis() - processStartTime);
        return response;
    }

    private static class SingletonHolder {
        private static final MessageHandler INSTANCE = new MessageHandler();
    }
}
