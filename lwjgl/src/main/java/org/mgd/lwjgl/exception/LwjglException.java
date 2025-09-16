package org.mgd.lwjgl.exception;

public class LwjglException extends Exception {
    public LwjglException(String message) {
        super(message);
    }

    public LwjglException(Throwable cause) {
        super(cause);
    }
}
