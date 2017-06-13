package com.mindflow.framework.rpc.transport;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface NettyServer {

    void bind() throws InterruptedException;

    void shutdown();

}
