package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.RecetteDto;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.gmel.coeur.persistence.RecetteJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class RecetteAf extends Af<RecetteDto, Recette> {
    protected RecetteAf(Path fichier) {
        super(new RecetteJao(), fichier);
    }
}
