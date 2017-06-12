package com.mindflow.framework.rpc.server;

import com.mindflow.framework.rpc.annotation.RpcService;
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

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {

        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);

                String interfaceName = rpcService.value().getName();
                String name = rpcService.name();
                String version = rpcService.version();

                logger.info("export interface:{}, name:{}, version:{}", interfaceName, name, version);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    private void start() {
        
    }

    @Override
    public void destroy() throws Exception {
        this.shutdown();
    }

    private void shutdown() {

    }
}
