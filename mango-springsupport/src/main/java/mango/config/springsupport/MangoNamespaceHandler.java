package mango.config.springsupport;

import mango.config.ApplicationConfig;
import mango.config.ProtocolConfig;
import mango.config.RegistryConfig;
import mango.util.ConcurrentHashSet;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.util.Set;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class MangoNamespaceHandler extends NamespaceHandlerSupport {
    public final static Set<String> protocolDefineNames = new ConcurrentHashSet<String>();
    public final static Set<String> registryDefineNames = new ConcurrentHashSet<String>();
    public final static Set<String> serviceConfigDefineNames = new ConcurrentHashSet<String>();
    public final static Set<String> referenceConfigDefineNames = new ConcurrentHashSet<String>();
    public final static Set<String> applicationConfigDefineNames = new ConcurrentHashSet<String>();

    @Override
    public void init() {
        registerBeanDefinitionParser("reference", new MangoBeanDefinitionParser(ReferenceConfigBean.class, false));
        registerBeanDefinitionParser("service", new MangoBeanDefinitionParser(ServiceConfigBean.class, true));
        registerBeanDefinitionParser("registry", new MangoBeanDefinitionParser(RegistryConfig.class, true));
        registerBeanDefinitionParser("protocol", new MangoBeanDefinitionParser(ProtocolConfig.class, true));
        registerBeanDefinitionParser("application", new MangoBeanDefinitionParser(ApplicationConfig.class, true));
    }
}
