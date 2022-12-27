package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.InventaireDto;
import org.mgd.gmel.coeur.objet.Inventaire;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;

public class InventaireAd extends Ad<InventaireDto, Inventaire, InventaireAf> {
    public InventaireAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected InventaireAf access(Path source) {
        return new InventaireAf(source);
    }

    public Inventaire inventaire(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, "{\"produitsQuantifier\":[]}").jo();
    }
}
