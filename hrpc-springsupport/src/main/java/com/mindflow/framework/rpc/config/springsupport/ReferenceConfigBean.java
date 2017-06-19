package com.mindflow.framework.rpc.config.springsupport;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.config.ProtocolConfig;
import com.mindflow.framework.rpc.config.ReferenceConfig;
import com.mindflow.framework.rpc.config.RegistryConfig;
import com.mindflow.framework.rpc.core.extension.ExtensionLoader;
import com.mindflow.framework.rpc.proxy.ClientInvocationHandler;
import com.mindflow.framework.rpc.proxy.ProxyFactory;
import com.mindflow.framework.rpc.proxy.jdk.JdkProxyFactory;
import com.mindflow.framework.rpc.registry.RegistryFactory;
import com.mindflow.framework.rpc.util.Constants;
import com.mindflow.framework.rpc.util.HRpcUtils;
import com.mindflow.framework.rpc.util.NetUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ReferenceConfigBean<T> extends ReferenceConfig<T> implements FactoryBean<T>, BeanFactoryAware, InitializingBean, DisposableBean {

    private transient BeanFactory beanFactory;
    private transient volatile boolean initialized;
    private ProxyFactory proxyFactory  = new JdkProxyFactory();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public T getObject() throws Exception {
        return get();
    }

    public T get() {
        if (proxy == null) {
            init();
        }
        return proxy;
    }

    private void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (interfaceName == null || interfaceName.length() == 0) {
            throw new IllegalStateException("<hrpc:reference interface=\"\" /> interface not allow null!");
        }
        try {
            interfaceClass = Class.forName(interfaceName, true, Thread.currentThread()
                    .getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("reference class not found", e);
        }

        //检查默认值
        if(timeout==null) {
            timeout = URLParamName.requestTimeout.getIntValue();
        }

        proxy = createProxy();
    }

    private T createProxy() {
        if(!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("<hrpc:reference interface=\"\" /> is not interface!");
        }

        RegistryConfig registryConfig = registries.get(0);

        InetAddress localAddress = NetUtils.getLocalAddress();
        URL url = new URL(URLParamName.codec.getValue(), localAddress.getHostAddress(), 0, interfaceClass.getName());
        url.addParameter(Constants.REGISTRY_PROTOCOL, registryConfig.getProtocol());
        url.addParameter(Constants.REGISTRY_ADDRESS, registryConfig.getAddress());
        url.addParameter(Constants.SIDE, "consumer");
        url.addParameter(Constants.TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(registryConfig.getProtocol());
        ClientInvocationHandler handler = new ClientInvocationHandler(url, registryFactory, timeout);
        return (T) proxyFactory.getProxy(interfaceClass, handler);
    }

    @Override
    public Class<?> getObjectType() {
        return getInterfaceClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        checkConfig();
    }

    private void checkConfig() {
        //检查依赖的配置
        if (CollectionUtils.isEmpty(getProtocols())) {
            for (String name : HRpcNamespaceHandler.protocolDefineNames) {
                ProtocolConfig pc = beanFactory.getBean(name, ProtocolConfig.class);
                if (pc == null) {
                    continue;
                }
                if (HRpcNamespaceHandler.protocolDefineNames.size() == 1) {
                    setProtocol(pc);
                } else if (pc.isDefault() != null && pc.isDefault().booleanValue()) {
                    setProtocol(pc);
                }
            }
        }
        if (getProtocols()==null || getProtocols().size()==0) {
            setProtocol(HRpcUtils.getDefaultProtocolConfig());
        }

        if (CollectionUtils.isEmpty(getRegistries())) {
            for (String name : HRpcNamespaceHandler.registryDefineNames) {
                RegistryConfig rc = beanFactory.getBean(name, RegistryConfig.class);
                if (rc == null) {
                    continue;
                }
                if (HRpcNamespaceHandler.registryDefineNames.size() == 1) {
                    setRegistry(rc);
                } else if (rc.isDefault() != null && rc.isDefault().booleanValue()) {
                    setRegistry(rc);
                }
            }
        }
        if (CollectionUtils.isEmpty(getRegistries())) {
            setRegistry(HRpcUtils.getDefaultRegistryConfig());
        }
    }

    @Override
    public void destroy() throws Exception {

    }

}
