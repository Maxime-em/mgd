package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.LivreCuisineDto;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.gmel.coeur.persistence.LivreCuisineJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class LivreCuisineAf extends Af<LivreCuisineDto, LivreCuisine> {
    protected LivreCuisineAf(Path fichier) {
        super(new LivreCuisineJao(), fichier);
    }
}
