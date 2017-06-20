package com.mindflow.framework.rpc.config.springsupport;

import com.mindflow.framework.rpc.config.ProtocolConfig;
import com.mindflow.framework.rpc.config.RegistryConfig;
import com.mindflow.framework.rpc.config.ServiceConfig;
import com.mindflow.framework.rpc.util.CollectionUtil;
import com.mindflow.framework.rpc.util.HRpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ServiceConfigBean<T> extends ServiceConfig<T> implements BeanFactoryAware,
        InitializingBean,
        ApplicationListener<ContextRefreshedEvent>,
        DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private transient BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (!isExported()) {
            export();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        logger.debug("check service interface:%s config", getInterfaceName());
        checkRegistryConfig();
        checkProtocolConfig();
    }

    @Override
    public void destroy() throws Exception {
        super.destroy0();
    }

    private void checkRegistryConfig() {
        if (CollectionUtil.isEmpty(getRegistries())) {
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
        if (CollectionUtil.isEmpty(getRegistries())) {
            setRegistry(HRpcUtils.getDefaultRegistryConfig());
        }
    }

    private void checkProtocolConfig() {
        if (CollectionUtil.isEmpty(getProtocols())) {
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
        if (CollectionUtil.isEmpty(getProtocols())) {
            setProtocol(HRpcUtils.getDefaultProtocolConfig());
        }
    }
}
