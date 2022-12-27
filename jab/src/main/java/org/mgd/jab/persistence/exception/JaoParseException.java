package org.mgd.jab.persistence.exception;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.Jao;

/**
 * Exception utilisée par {@link Jao} lors du chargement d'objet métier de type
 * {@link Jo}.
 *
 * @author Maxime
 */
public class JaoParseException extends Exception {
    public JaoParseException(String message) {
        super(message);
    }

    public JaoParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JaoParseException(Throwable cause) {
        super(cause);
    }
}
