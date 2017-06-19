package com.mindflow.framework.rpc.config.springsupport;

import com.mindflow.framework.rpc.config.ProtocolConfig;
import com.mindflow.framework.rpc.config.RegistryConfig;
import com.mindflow.framework.rpc.config.ServiceConfig;
import com.mindflow.framework.rpc.util.ConcurrentHashSet;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.util.Set;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class HRpcNamespaceHandler extends NamespaceHandlerSupport {
    public final static Set<String> protocolDefineNames = new ConcurrentHashSet<String>();
    public final static Set<String> registryDefineNames = new ConcurrentHashSet<String>();
    public final static Set<String> serviceConfigDefineNames = new ConcurrentHashSet<String>();
    public final static Set<String> referenceConfigDefineNames = new ConcurrentHashSet<String>();

    @Override
    public void init() {
        registerBeanDefinitionParser("reference", new HRpcBeanDefinitionParser(ReferenceConfigBean.class, false));
        registerBeanDefinitionParser("service", new HRpcBeanDefinitionParser(ServiceConfig.class, true));
        registerBeanDefinitionParser("registry", new HRpcBeanDefinitionParser(RegistryConfig.class, true));
        registerBeanDefinitionParser("protocol", new HRpcBeanDefinitionParser(ProtocolConfig.class, true));
    }
}
