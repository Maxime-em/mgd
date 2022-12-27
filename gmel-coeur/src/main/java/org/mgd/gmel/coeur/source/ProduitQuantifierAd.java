package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.ProduitQuantifierDto;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;

public class ProduitQuantifierAd extends Ad<ProduitQuantifierDto, ProduitQuantifier, ProduitQuantifierAf> {
    public ProduitQuantifierAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected ProduitQuantifierAf access(Path source) {
        return new ProduitQuantifierAf(source);
    }

    public ProduitQuantifier produitQuantifier(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, "{\"produit\":{\"nom\":\"produit\"},\"quantite\":{\"mesure\":\"MASSE\",\"valeur\":1}}").jo();
    }
}
