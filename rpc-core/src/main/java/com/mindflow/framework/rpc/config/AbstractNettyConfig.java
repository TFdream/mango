package com.mindflow.framework.rpc.config;

/**
 * @author Ricky Fung
 */
public class AbstractNettyConfig implements NettyConfig {
    private String address;
    private String host;
    private int port = 21918;
    private int maxFrameLength = 1<<20;
    private int lengthFieldLength = 15;
    private int backlogSize = 128;
    private int receivedBufferSize = 1024*32;
    private int sendBufferSize = 1024*32;

    @Override
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getMaxFrameLength() {
        return maxFrameLength;
    }

    public void setMaxFrameLength(int maxFrameLength) {
        this.maxFrameLength = maxFrameLength;
    }

    @Override
    public int getLengthFieldLength() {
        return lengthFieldLength;
    }

    public void setLengthFieldLength(int lengthFieldLength) {
        this.lengthFieldLength = lengthFieldLength;
    }

    @Override
    public int getBacklogSize() {
        return backlogSize;
    }

    public void setBacklogSize(int backlogSize) {
        this.backlogSize = backlogSize;
    }

    @Override
    public int getReceivedBufferSize() {
        return receivedBufferSize;
    }

    public void setReceivedBufferSize(int receivedBufferSize) {
        this.receivedBufferSize = receivedBufferSize;
    }

    @Override
    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }
}
