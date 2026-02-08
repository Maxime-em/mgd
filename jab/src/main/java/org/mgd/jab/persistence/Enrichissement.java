package org.mgd.jab.persistence;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.exception.VerificationException;

@FunctionalInterface
public interface Enrichissement<O extends Jo> {
    void faire(O objet) throws JaoExecutionException, JaoParseException, VerificationException;
}
