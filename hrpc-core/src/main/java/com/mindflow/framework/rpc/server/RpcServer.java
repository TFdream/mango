package com.mindflow.framework.rpc.server;

import com.google.common.base.Preconditions;
import com.mindflow.framework.rpc.annotation.RpcService;
import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.config.NettyServerConfig;
import com.mindflow.framework.rpc.config.RegistryConfig;
import com.mindflow.framework.rpc.core.extension.ExtensionLoader;
import com.mindflow.framework.rpc.registry.RegistryFactory;
import com.mindflow.framework.rpc.transport.NettyServer;
import com.mindflow.framework.rpc.transport.NettyServerImpl;
import com.mindflow.framework.rpc.util.Constants;
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

    private RegistryConfig registryConfig;
    private String protocol;
    private String serverAddress;   //ip:port
    private NettyServer nettyServer;
    private transient ApplicationContext ctx;
    private final List<URL> urls = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    private void start() throws InterruptedException {

        Preconditions.checkNotNull(this.serverAddress);
        Preconditions.checkNotNull(this.registryConfig);
        Preconditions.checkNotNull(this.registryConfig.getProtocol());
        Preconditions.checkNotNull(this.registryConfig.getAddress());

        String[] arr = this.serverAddress.split(":");
        String host = arr[0];
        int port = Integer.parseInt(arr[1]);

        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);

                Class<?> interfaceClass = rpcService.value();
                if(!interfaceClass.isAssignableFrom(serviceBean.getClass())) {
                    throw new IllegalArgumentException(serviceBean.getClass() +" is not "+interfaceClass+" sub class!");
                }
                String interfaceName = rpcService.value().getName();
                String version = rpcService.version();
                String group = rpcService.version();

                URL url = new URL(URLParamName.codec.getValue(), host, port, interfaceName);
                url.addParameter(URLParamName.version.getName(), version);
                url.addParameter(URLParamName.group.getName(), group!=null ? group : URLParamName.group.getValue());
                url.addParameter(URLParamName.serializer.getName(), URLParamName.serializer.getValue());
                url.addParameter(Constants.REGISTRY_PROTOCOL, this.registryConfig.getProtocol());
                url.addParameter(Constants.REGISTRY_ADDRESS, this.registryConfig.getAddress());
                url.addParameter(Constants.SIDE, "provider");
                url.addParameter(Constants.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
                urls.add(url);

                MessageHandler.getInstance().addServiceBean(interfaceName, serviceBean);
                logger.info("export interface:{}, group:{}, version:{}", interfaceName, group, version);
            }
        }

        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(this.registryConfig.getProtocol());
        for (URL url : urls) {
            try {
                registryFactory.getRegistry(url).register(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        NettyServerConfig config = new NettyServerConfig();
        config.setPort(port);
        nettyServer = new NettyServerImpl(config);

        nettyServer.bind();
    }

    @Override
    public void destroy() throws Exception {
        nettyServer.shutdown();
    }

}
