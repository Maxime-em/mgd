package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.ProduitQuantifierDto;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class ProduitQuantifierAd extends Ad<ProduitQuantifierDto, ProduitQuantifier, ProduitQuantifierAf> {
    public ProduitQuantifierAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected ProduitQuantifierAf access(Path source) {
        return new ProduitQuantifierAf(source);
    }

    public <F> ProduitQuantifier produitQuantifier(String nom, UUID produitIdentifiant, Path cheminFourunisseur, Class<F> classeFournisseur) throws IOException, JaoExecutionException, JaoParseException {
        String produit = "\"identifiant\":\"" + produitIdentifiant + "\",\"chemin\":\"" + cheminFourunisseur.toAbsolutePath().toString().replace("\\", "\\\\") + "\",\"classeFournisseur\":\"" + classeFournisseur.getName() + "\"";
        String produitQuantifier = "{\"produit\":{" + produit + "},\"quantite\":{\"mesure\":\"MASSE\",\"valeur\":1}}";
        return access(nom, produitQuantifier).jo();
    }
}
