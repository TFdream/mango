package com.mindflow.framework.rpc.config.springsupport;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.config.ProtocolConfig;
import com.mindflow.framework.rpc.config.ReferenceConfig;
import com.mindflow.framework.rpc.config.RegistryConfig;
import com.mindflow.framework.rpc.proxy.ProxyFactory;
import com.mindflow.framework.rpc.proxy.RpcInvoker;
import com.mindflow.framework.rpc.proxy.jdk.JdkProxyFactory;
import com.mindflow.framework.rpc.util.HRpcUtils;
import com.mindflow.framework.rpc.util.NetUtils;
import com.mindflow.framework.rpc.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private transient BeanFactory beanFactory;
    private transient volatile boolean initialized;
    private ProxyFactory proxyFactory  = new JdkProxyFactory();
    private RpcInvoker invoker;

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

        proxy = createProxy();
    }

    private T createProxy() {
        if(!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("<hrpc:reference interface=\"\" /> is not interface!");
        }

        RegistryConfig registryConfig = registries.get(0);
        ProtocolConfig protocolConfig = protocols.get(0);

        InetAddress localAddress = NetUtils.getLocalAddress();
        URL url = new URL(URLParamName.codec.getValue(), localAddress.getHostAddress(), 0, interfaceClass.getName());
        url.addParameter(URLParamName.registryProtocol.getName(), registryConfig.getProtocol());
        url.addParameter(URLParamName.registryAddress.getName(), registryConfig.getAddress());
        url.addParameter(URLParamName.serialization.getName(), StringUtils.isNotEmpty(protocolConfig.getSerialization()) ? protocolConfig.getSerialization(): URLParamName.serialization.getValue());
        url.addParameter(URLParamName.version.getName(), StringUtils.isNotEmpty(version) ? version : URLParamName.version.getValue());
        url.addParameter(URLParamName.group.getName(), StringUtils.isNotEmpty(group) ? group : URLParamName.group.getValue());
        url.addParameter(URLParamName.side.getName(), "consumer");
        url.addParameter(URLParamName.timestamp.getName(), String.valueOf(System.currentTimeMillis()));

        invoker = new RpcInvoker(url, timeout);
        return (T) proxyFactory.getProxy(interfaceClass, invoker);
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

        logger.info("checkConfig");
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

        if(StringUtils.isEmpty(getGroup())) {
            setGroup(URLParamName.group.getValue());
        }
        if(StringUtils.isEmpty(getVersion())) {
            setVersion(URLParamName.version.getValue());
        }

        if(getTimeout()==null) {
            setTimeout(URLParamName.requestTimeout.getIntValue());
        }
        if(getRetries()==null) {
            setRetries(URLParamName.retries.getIntValue());
        }
    }

    @Override
    public void destroy() throws Exception {
        invoker.close();
    }

}
