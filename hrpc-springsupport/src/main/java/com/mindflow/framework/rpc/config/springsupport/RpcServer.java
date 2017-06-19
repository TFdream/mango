package com.mindflow.framework.rpc.config.springsupport;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.config.NettyServerConfig;
import com.mindflow.framework.rpc.config.ProtocolConfig;
import com.mindflow.framework.rpc.config.RegistryConfig;
import com.mindflow.framework.rpc.config.ServiceConfig;
import com.mindflow.framework.rpc.core.MessageHandler;
import com.mindflow.framework.rpc.core.extension.ExtensionLoader;
import com.mindflow.framework.rpc.registry.RegistryFactory;
import com.mindflow.framework.rpc.transport.NettyServer;
import com.mindflow.framework.rpc.transport.NettyServerImpl;
import com.mindflow.framework.rpc.util.Constants;
import com.mindflow.framework.rpc.util.HRpcUtils;
import com.mindflow.framework.rpc.util.NetUtils;
import com.mindflow.framework.rpc.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class RpcServer implements ApplicationContextAware, InitializingBean, DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private NettyServer nettyServer;
    private transient ApplicationContext ctx;
    private final List<URL> urls = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
        logger.info("[set ApplicationContext]");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    private void start() throws InterruptedException {

        ProtocolConfig protocolConfig = null;
        for (String name : HRpcNamespaceHandler.protocolDefineNames) {
            ProtocolConfig pc = ctx.getBean(name, ProtocolConfig.class);
            if (pc == null) {
                continue;
            }
            if (HRpcNamespaceHandler.protocolDefineNames.size() == 1) {
                protocolConfig = pc;
            } else if (pc.isDefault() != null && pc.isDefault().booleanValue()) {
                protocolConfig = pc;
            }
        }
        if(protocolConfig==null) {
            protocolConfig = HRpcUtils.getDefaultProtocolConfig();
        }

        if(StringUtils.isEmpty(protocolConfig.getHost())) {
            protocolConfig.setHost(NetUtils.getLocalAddress().getHostAddress());
        }
        if(protocolConfig.getPort()==null) {
            protocolConfig.setPort(Constants.DEFAULT_PORT);
        }

        //注册中心
        RegistryConfig registryConfig = null;
        for (String name : HRpcNamespaceHandler.registryDefineNames) {
            RegistryConfig rc = ctx.getBean(name, RegistryConfig.class);
            if (rc == null) {
                continue;
            }
            if (HRpcNamespaceHandler.registryDefineNames.size() == 1) {
                registryConfig = rc;
            } else if (rc.isDefault() != null && rc.isDefault().booleanValue()) {
                registryConfig = rc;
            }
        }
        if(registryConfig==null) {
            registryConfig = HRpcUtils.getDefaultRegistryConfig();
        }

        //扫描Service
        Map<String, ServiceConfig> serviceBeanMap = ctx.getBeansOfType(ServiceConfig.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (ServiceConfig serviceConfig : serviceBeanMap.values()) {
                String interfaceName = serviceConfig.getInterfaceName();
                String version = serviceConfig.getVersion();
                String group = serviceConfig.getGroup();
                Object ref = serviceConfig.getRef();

                if (ref == null) {
                    throw new IllegalStateException("ref not allow null!");
                }
                Class<?> interfaceClass;
                try {
                    interfaceClass = Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
                if(!interfaceClass.isAssignableFrom(ref.getClass())) {
                    throw new IllegalArgumentException(serviceConfig.getClass() +" is not "+interfaceClass+" sub class!");
                }

                URL url = new URL(protocolConfig.getName(), protocolConfig.getHost(), protocolConfig.getPort(), interfaceName);
                url.addParameter(URLParamName.version.getName(), version);
                url.addParameter(URLParamName.group.getName(), group!=null ? group : URLParamName.group.getValue());
                url.addParameter(URLParamName.serializer.getName(), URLParamName.serializer.getValue());
                url.addParameter(Constants.REGISTRY_PROTOCOL, registryConfig.getProtocol());
                url.addParameter(Constants.REGISTRY_ADDRESS, registryConfig.getAddress());
                url.addParameter(Constants.SIDE, "provider");
                url.addParameter(Constants.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
                urls.add(url);

                MessageHandler.getInstance().addServiceBean(interfaceName, ref);
                logger.info("export interface:{}, group:{}, version:{}", interfaceName, group, version);
            }
        }

        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(registryConfig.getProtocol());
        for (URL url : urls) {
            try {
                registryFactory.getRegistry(url).register(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        NettyServerConfig config = new NettyServerConfig();
        config.setPort(protocolConfig.getPort());
        nettyServer = new NettyServerImpl(config);

        nettyServer.bind();
    }

    @Override
    public void destroy() throws Exception {
        nettyServer.shutdown();
    }

}
