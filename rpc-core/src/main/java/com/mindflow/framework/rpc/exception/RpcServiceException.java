package com.mindflow.framework.rpc.exception;

/**
 * wrapper service exception.
 *
 * @author Ricky Fung
 */
public class RpcServiceException extends AbstractRpcException {

    private static final long serialVersionUID = -3361435023080270457L;

    public RpcServiceException() {
    }

    public RpcServiceException(String message) {
        super(message);
    }

    public RpcServiceException(Throwable cause) {
        super(cause);
    }

    public RpcServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
