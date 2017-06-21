package mango.proxy;

import mango.common.URLParam;
import mango.core.DefaultRequest;
import mango.core.Response;
import mango.exception.RpcFrameworkException;
import mango.exception.RpcServiceException;
import mango.rpc.Invoker;
import mango.util.Constants;
import mango.util.ExceptionUtil;
import mango.util.RequestIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ReferenceInvocationHandler<T> implements InvocationHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Invoker<T> invoker;

    public ReferenceInvocationHandler(Invoker<T> invoker) {
        this.invoker = invoker;
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
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setArguments(args);
        request.setType(Constants.REQUEST_SYNC);
        //调用参数
        request.setAttachment(URLParam.version.getName(), invoker.getServiceUrl().getVersion());
        request.setAttachment(URLParam.group.getName(), invoker.getServiceUrl().getGroup());
        try {
            Response resp = invoker.call(request);
            return getValue(resp);
        } catch (RuntimeException e) {
            if (ExceptionUtil.isBizException(e)) {
                Throwable t = e.getCause();
                if (t != null && t instanceof Exception) {
                    throw t;
                } else {
                    String msg =
                            t == null ? "biz exception cause is null" : ("biz exception cause is throwable error:" + t.getClass()
                                    + ", errmsg:" + t.getMessage());
                    throw new RpcServiceException(msg);
                }
            } else {
                logger.error(this.getClass().getSimpleName()+" invoke Error: uri=" + invoker.getServiceUrl().getUri(), e);
                throw e;
            }
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
