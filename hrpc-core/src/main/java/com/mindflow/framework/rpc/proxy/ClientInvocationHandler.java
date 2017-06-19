package com.mindflow.framework.rpc.proxy;

import com.mindflow.framework.rpc.cluster.loadbalance.LoadBalance;
import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.config.NettyClientConfig;
import com.mindflow.framework.rpc.core.DefaultRequest;
import com.mindflow.framework.rpc.core.Response;
import com.mindflow.framework.rpc.core.extension.ExtensionLoader;
import com.mindflow.framework.rpc.exception.RpcFrameworkException;
import com.mindflow.framework.rpc.registry.NotifyListener;
import com.mindflow.framework.rpc.registry.Registry;
import com.mindflow.framework.rpc.registry.RegistryFactory;
import com.mindflow.framework.rpc.transport.NettyClient;
import com.mindflow.framework.rpc.transport.NettyClientImpl;
import com.mindflow.framework.rpc.util.Constants;
import com.mindflow.framework.rpc.util.RequestIdGenerator;
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
public class ClientInvocationHandler implements InvocationHandler, NotifyListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private NettyClient nettyClient;
    private RegistryFactory registryFactory;
    private long timeoutInMillis;
    private URL url;
    private List<URL> urls;
    private LoadBalance loadBalanceStrategy;

    public ClientInvocationHandler(URL url, RegistryFactory registryFactory, long timeoutInMillis) {
        this.url = url;
        this.registryFactory = registryFactory;
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
        this.loadBalanceStrategy = ExtensionLoader.getExtensionLoader(LoadBalance.class).getDefaultExtension();
        try {
            Registry registry = this.registryFactory.getRegistry(url);
            this.urls = registry.discover(url);
            if(this.urls==null || this.urls.isEmpty()) {
                throw new IllegalStateException("no provider for url:"+url);
            }
            //
            registry.subscribe(url, this);
        } catch (Exception e) {
            throw new RuntimeException("init error", e);
        }

        nettyClient = new NettyClientImpl(new NettyClientConfig());
        nettyClient.start();
    }

    @Override
    public void notify(URL registryUrl, List<URL> urls) {
        logger.info("client notify from %s", registryUrl);
    }
}
