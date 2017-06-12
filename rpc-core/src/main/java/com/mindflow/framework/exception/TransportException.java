package com.mindflow.framework.exception;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class TransportException extends RuntimeException {

    public TransportException() {
    }

    public TransportException(String message) {
        super(message);
    }

    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
