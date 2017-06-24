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
public interface NettyClient extends Endpoint {

    Response invokeSync(final Request request)
            throws InterruptedException, TransportException;

    ResponseFuture invokeAsync(final Request request)
            throws InterruptedException, TransportException;

    void invokeOneway(final Request request)
            throws InterruptedException, TransportException;

}
