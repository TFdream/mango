package com.mindflow.framework.rpc.config;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class NettyServerConfig extends AbstractNettyConfig {

    private int corePoolSize = 100;
    private int maximumPoolSize = 200;
    private int keepAliveTimeSeconds = 10;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getKeepAliveTimeSeconds() {
        return keepAliveTimeSeconds;
    }

    public void setKeepAliveTimeSeconds(int keepAliveTimeSeconds) {
        this.keepAliveTimeSeconds = keepAliveTimeSeconds;
    }
}
