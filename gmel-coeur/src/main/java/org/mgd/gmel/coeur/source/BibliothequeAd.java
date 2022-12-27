package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.BibliothequeDto;
import org.mgd.gmel.coeur.objet.Bibliotheque;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;

public class BibliothequeAd extends Ad<BibliothequeDto, Bibliotheque, BibliothequeAf> {
    public BibliothequeAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected BibliothequeAf access(Path source) {
        return new BibliothequeAf(source);
    }

    public Bibliotheque bibliotheque(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, "{\"livresCuisine\":[]}").jo();
    }
}
