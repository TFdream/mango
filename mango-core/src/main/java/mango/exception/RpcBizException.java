package mango.exception;

/**
 * wrapper biz exception.
 *
 * @author Ricky Fung
 */
public class RpcBizException extends AbstractRpcException {

    private static final long serialVersionUID = -812786666451764184L;

    public RpcBizException() {
    }

    public RpcBizException(String message) {
        super(message);
    }

    public RpcBizException(Throwable cause) {
        super(cause);
    }

    public RpcBizException(String message, Throwable cause) {
        super(message, cause);
    }
}
