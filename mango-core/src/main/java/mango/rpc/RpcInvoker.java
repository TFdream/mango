package mango.rpc;

import mango.cluster.loadbalance.LoadBalance;
import mango.common.Closeable;
import mango.common.URL;
import mango.common.URLParam;
import mango.config.NettyClientConfig;
import mango.core.DefaultRequest;
import mango.core.Response;
import mango.core.extension.ExtensionLoader;
import mango.exception.RpcFrameworkException;
import mango.registry.NotifyListener;
import mango.registry.Registry;
import mango.registry.RegistryFactory;
import mango.transport.NettyClient;
import mango.transport.NettyClientImpl;
import mango.util.Constants;
import mango.util.RequestIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class RpcInvoker implements InvocationHandler, NotifyListener, Closeable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private NettyClient nettyClient;
    private long timeoutInMillis;
    private URL url;
    private List<URL> urls;
    private LoadBalance loadBalanceStrategy;

    public RpcInvoker(URL url, long timeoutInMillis) {
        this.url = url;
        this.timeoutInMillis = timeoutInMillis;

        init();
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
            //load-balance
            URL url = loadBalanceStrategy.select(urls);
            String address = String.format("%s:%d", url.getHost(), url.getPort());
            logger.info("LB select: {}", address);
            Response resp = nettyClient.invokeSync(address, request, timeoutInMillis);
            return getValue(resp);
        } catch (RuntimeException e) {
            throw new RpcFrameworkException("invoke exception", e);
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

    private void init() {

        String regProtocol = url.getParameter(URLParam.registryProtocol.getName());
        String regAddress = url.getParameter(URLParam.registryAddress.getName());
        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(regProtocol);
        this.loadBalanceStrategy = ExtensionLoader.getExtensionLoader(LoadBalance.class).getDefaultExtension();
        try {
            Registry registry = registryFactory.getRegistry(url);
            this.urls = registry.discover(url);
            if(this.urls==null || this.urls.isEmpty()) {
                throw new IllegalStateException("no provider for url:"+url);
            }

            //订阅服务
            registry.subscribe(url, this);
        } catch (Exception e) {
            throw new RpcFrameworkException("Unable discover/subscribe service:"+url.getPath() + " from ["+regProtocol+":"+regAddress+"]", e);
        }

        nettyClient = new NettyClientImpl(new NettyClientConfig());
        nettyClient.start();
    }

    @Override
    public void notify(URL registryUrl, List<URL> urls) {
        logger.info("client notify from %s", registryUrl);
    }

    @Override
    public void close() throws Exception {

    }
}
