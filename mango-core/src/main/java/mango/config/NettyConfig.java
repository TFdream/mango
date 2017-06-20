package mango.config;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface NettyConfig {

    String getAddress();

    String getHost();

    int getPort();

    /**Netty codec**/
    int getMaxFrameLength();

    int getLengthFieldLength();

    /**Netty Channel config**/
    int getBacklogSize();

    int getReceivedBufferSize();

    int getSendBufferSize();
}
