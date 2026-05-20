package org.mgd.jab.persistence;

import org.mgd.jab.dto.HerosDto;
import org.mgd.jab.objet.Heros;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.persistence.exception.JaoRuntimeException;
import org.mgd.jab.utilitaire.exception.VerificationException;

/**
 * Accès aux données des héros dont l'enrichissement d'un « Rebelle » lève une erreur d'exécution.
 */
public class HerosRebelleJao extends HerosJao {
    public static final String REBELLE = "Rebelle";

    @Override
    public void enrichir(HerosDto dto, Heros objet) throws JaoExecutionException, JaoParseException, VerificationException {
        if (REBELLE.equals(dto.nom)) {
            throw new JaoRuntimeException(new IllegalStateException("Le héros se rebelle."));
        }
        super.enrichir(dto, objet);
    }
}
