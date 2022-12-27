package org.mgd.commun;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation utilisée dans l'inspection d'IntelliJ pour faire taire les erreurs "Unused declaration"
 *
 * @author Maxime
 */
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
public @interface EntryPoint {
}
