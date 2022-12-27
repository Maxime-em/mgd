package org.mgd.gmel.javafx.composant.exception;

public class ComposantException extends RuntimeException {
    public ComposantException(String message) {
        super(message);
    }

    public ComposantException(Throwable cause) {
        super(cause);
    }
}
