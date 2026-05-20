package org.mgd.jab.source;

import org.mgd.jab.dto.HerosDto;
import org.mgd.jab.objet.Heros;
import org.mgd.jab.persistence.Jao;

import java.nio.file.Path;

/**
 * Fichier JSON d'un héros, objet de test.
 */
public class HerosAf extends Af<HerosDto, Heros> {
    public HerosAf(Jao<HerosDto, Heros> jao, Path fichier) {
        super(jao, fichier);
    }
}
