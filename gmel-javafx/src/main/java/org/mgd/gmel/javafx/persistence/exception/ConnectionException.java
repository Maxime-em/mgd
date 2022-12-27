package org.mgd.gmel.javafx.persistence.exception;

public class ConnectionException extends Exception {
    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }
}
