package com.mindflow.framework.rpc.config.springsupport;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.config.NettyServerConfig;
import com.mindflow.framework.rpc.config.ProtocolConfig;
import com.mindflow.framework.rpc.config.RegistryConfig;
import com.mindflow.framework.rpc.config.ServiceConfig;
import com.mindflow.framework.rpc.core.MessageHandler;
import com.mindflow.framework.rpc.core.extension.ExtensionLoader;
import com.mindflow.framework.rpc.exception.RpcFrameworkException;
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
    private transient List<URL> urls;

    private ProtocolConfig protocolConfig;
    private RegistryConfig registryConfig;

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

        this.protocolConfig = checkProtocolConfig();
        //注册中心
        this.registryConfig = checkRegistryConfig();

        //扫描Service
        urls = loadRegistryUrls();

        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(registryConfig.getProtocol());
        for (URL url : urls) {
            try {
                registryFactory.getRegistry(url).register(url);
            } catch (Exception e) {
                throw new RpcFrameworkException("Unable register url:"+url
                        +" to ["+registryConfig.getProtocol()+":"+registryConfig.getAddress()+"]", e);
            }
        }

        NettyServerConfig config = new NettyServerConfig();
        config.setPort(protocolConfig.getPort());
        nettyServer = new NettyServerImpl(config);

        nettyServer.bind();
    }

    private List<URL> loadRegistryUrls() {
        List<URL> urlList = new ArrayList<>();
        Map<String, ServiceConfig> serviceBeanMap = ctx.getBeansOfType(ServiceConfig.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (ServiceConfig serviceConfig : serviceBeanMap.values()) {
                String interfaceName = serviceConfig.getInterfaceName();
                String version = serviceConfig.getVersion();
                String group = serviceConfig.getGroup();
                String serializer = protocolConfig.getSerialization();
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
                url.addParameter(URLParamName.version.getName(), StringUtils.isNotEmpty(version) ? version : URLParamName.version.getValue());
                url.addParameter(URLParamName.group.getName(), StringUtils.isNotEmpty(group) ? group : URLParamName.group.getValue());
                url.addParameter(URLParamName.serialization.getName(), StringUtils.isNotEmpty(serializer) ? serializer: URLParamName.serialization.getValue());
                url.addParameter(URLParamName.registryProtocol.getName(), registryConfig.getProtocol());
                url.addParameter(URLParamName.registryAddress.getName(), registryConfig.getAddress());
                url.addParameter(URLParamName.side.getName(), "provider");
                url.addParameter(URLParamName.timestamp.getName(), String.valueOf(System.currentTimeMillis()));
                urlList.add(url);

                MessageHandler.getInstance().addServiceBean(interfaceName, ref);
                logger.info("export interface:{}, group:{}, version:{}", interfaceName, group, version);
            }
        }
        return urlList;
    }

    private RegistryConfig checkRegistryConfig() {
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
        return registryConfig;
    }

    private ProtocolConfig checkProtocolConfig() {
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

        return protocolConfig;
    }

    @Override
    public void destroy() throws Exception {
        nettyServer.shutdown();
    }

}
