package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.ProduitDto;
import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;

public class ProduitAd extends Ad<ProduitDto, Produit, ProduitAf> {
    public ProduitAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected ProduitAf access(Path source) {
        return new ProduitAf(source);
    }

    public Produit produit(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, "{\"nom\":\"Produit\"}").jo();
    }
}
