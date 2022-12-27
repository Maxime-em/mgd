package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.RecetteDto;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;

public class RecetteAd extends Ad<RecetteDto, Recette, RecetteAf> {
    public RecetteAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected RecetteAf access(Path source) {
        return new RecetteAf(source);
    }

    public Recette recette(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, "{\"nom\":\"Recette\",\"nombrePersonnes\":1,\"produitsQuantifier\":[]}").jo();
    }
}
