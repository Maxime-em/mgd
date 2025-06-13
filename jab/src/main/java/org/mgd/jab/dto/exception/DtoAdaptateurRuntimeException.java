package org.mgd.jab.dto.exception;

/**
 * Exception utilisée par les adaptateurs de l'analyseur {@link org.mgd.jab.JabSauvegarde#gsonSauvegarde} lors de la récupération
 * des objets {@link org.mgd.jab.dto.Dto} depuis les fichiers JSON.
 *
 * @author Maxime
 */
public class DtoAdaptateurRuntimeException extends RuntimeException {
    public DtoAdaptateurRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
