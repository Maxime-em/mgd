package org.mgd.jab.source;

import org.mgd.jab.dto.HerosDto;
import org.mgd.jab.objet.Heros;
import org.mgd.jab.persistence.HerosJao;

import java.nio.file.Path;

/**
 * Dossier de la Ligue de Justice contenant un fichier JSON par héros, objet de test.
 */
public class LigueAd extends Ad<HerosDto, Heros, HerosAf> {
    public LigueAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected HerosAf access(Path source) {
        return new HerosAf(new HerosJao(), source);
    }
}
