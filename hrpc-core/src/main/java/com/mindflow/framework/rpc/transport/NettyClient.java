package com.mindflow.framework.rpc.transport;

import com.mindflow.framework.rpc.core.Request;
import com.mindflow.framework.rpc.core.Response;
import com.mindflow.framework.rpc.core.ResponseFuture;
import com.mindflow.framework.rpc.exception.TransportException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface NettyClient {

    void start();

    Response invokeSync(String address, final Request request, final long timeoutInMillis)
            throws InterruptedException, TransportException;

    ResponseFuture invokeAsync(String address, final Request request, final long timeoutInMillis)
            throws InterruptedException, TransportException;

    void invokeOneway(String address, final Request request, final long timeoutInMillis)
            throws InterruptedException, TransportException;

    void shutdown();
}
