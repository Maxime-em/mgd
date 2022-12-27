package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.LivreCuisineDto;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;

public class LivreCuisineAd extends Ad<LivreCuisineDto, LivreCuisine, LivreCuisineAf> {
    public LivreCuisineAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected LivreCuisineAf access(Path dossier) {
        return new LivreCuisineAf(dossier);
    }

    public LivreCuisine livreCuisine(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, "{\"nom\":\"Livre de cuisine\",\"recettes\":[]}").jo();
    }
}
