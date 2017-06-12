package com.mindflow.framework.rpc.config;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class NettyServerConfig implements NettyConfig {

    private int corePoolSize = 100;
    private int maximumPoolSize = 200;
    private int keepAliveTimeSeconds = 10;

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public int getMaxFrameLength() {
        return 0;
    }

    @Override
    public int getLengthFieldLength() {
        return 0;
    }

    @Override
    public int getBacklogSize() {
        return 0;
    }

    @Override
    public int getReceivedBufferSize() {
        return 0;
    }

    @Override
    public int getSendBufferSize() {
        return 0;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public long getKeepAliveTimeSeconds() {
        return keepAliveTimeSeconds;
    }
}
