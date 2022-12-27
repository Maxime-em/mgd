package org.mgd.jab.exception;

import org.mgd.jab.Jab;

/**
 * Exception utilisée par {@link Jab}
 *
 * @author Maxime
 */
public class JabException extends Exception {
    public JabException(String message) {
        super(message);
    }

    public JabException(String message, Throwable cause) {
        super(message, cause);
    }

    public JabException(Throwable cause) {
        super(cause);
    }
}
