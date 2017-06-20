package mango.exception;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class AbstractRpcException extends RuntimeException {

    private static final long serialVersionUID = -5396234117892123976L;

    public AbstractRpcException() {
    }

    public AbstractRpcException(String message) {
        super(message);
    }

    public AbstractRpcException(Throwable cause) {
        super(cause);
    }

    public AbstractRpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
