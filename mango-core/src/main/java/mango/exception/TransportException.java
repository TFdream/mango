package mango.exception;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class TransportException extends AbstractRpcException {

    private static final long serialVersionUID = 1391824218667687554L;

    public TransportException() {
    }

    public TransportException(String message) {
        super(message);
    }

    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
