package com.mindflow.framework.rpc.server;

import com.mindflow.framework.rpc.annotation.RpcService;
import com.mindflow.framework.rpc.config.NettyServerConfig;
import com.mindflow.framework.rpc.registry.RegistryService;
import com.mindflow.framework.rpc.transport.NettyServer;
import com.mindflow.framework.rpc.transport.NettyServerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class RpcServer implements ApplicationContextAware, InitializingBean, DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RegistryService registryService;
    private String protocol;
    private int port;
    private NettyServer nettyServer;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {

        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);

                Class<?> interfaceClass = rpcService.value();
                if(!interfaceClass.isAssignableFrom(serviceBean.getClass())) {
                    throw new IllegalArgumentException(serviceBean.getClass() +" is not "+interfaceClass+" sub class!");
                }
                String interfaceName = rpcService.value().getName();
                String name = rpcService.name();
                String version = rpcService.version();

                MessageHandler.getInstance().addServiceBean(interfaceName, serviceBean);
                logger.info("export interface:{}, name:{}, version:{}", interfaceName, name, version);
            }
        }
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    private void start() throws InterruptedException {

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
