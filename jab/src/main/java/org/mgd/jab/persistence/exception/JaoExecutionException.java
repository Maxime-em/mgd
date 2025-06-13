package org.mgd.jab.persistence.exception;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.Jao;

/**
 * Exception utilisée par {@link Jao} lors de l'exécution de diverses tâches en dehors du
 * chargement d'objet métier de type {@link Jo}.
 *
 * @author Maxime
 */
public class JaoExecutionException extends Exception {
    public JaoExecutionException(String message) {
        super(message);
    }

    public JaoExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JaoExecutionException(Throwable cause) {
        super(cause);
    }
}
