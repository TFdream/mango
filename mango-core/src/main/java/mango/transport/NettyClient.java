package mango.transport;

import mango.core.Request;
import mango.core.Response;
import mango.core.ResponseFuture;
import mango.exception.TransportException;

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
