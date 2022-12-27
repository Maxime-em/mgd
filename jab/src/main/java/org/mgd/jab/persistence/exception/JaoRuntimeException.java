package org.mgd.jab.persistence.exception;

import org.mgd.jab.persistence.Jao;

/**
 * Exception utilisée par {@link Jao} dans les méthodes manipulant les
 * {@link java.util.stream.Stream}
 *
 * @author Maxime
 */
public class JaoRuntimeException extends RuntimeException {
    public JaoRuntimeException(Throwable cause) {
        super(cause);
    }
}
