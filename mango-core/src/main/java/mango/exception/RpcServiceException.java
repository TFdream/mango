package mango.exception;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class RpcServiceException extends AbstractRpcException {

    private static final long serialVersionUID = -6585936752307757973L;

    public RpcServiceException() {
    }

    public RpcServiceException(Throwable cause) {
        super(cause);
    }

    public RpcServiceException(String message) {
        super(message);
    }

    public RpcServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
