package com.mindflow.framework.rpc.config;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class NettyClientConfig implements NettyConfig {

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
}
