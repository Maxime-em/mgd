package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.ProduitDto;
import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.gmel.coeur.persistence.ProduitJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class ProduitAf extends Af<ProduitDto, Produit> {
    protected ProduitAf(Path fichier) {
        super(new ProduitJao(), fichier);
    }
}
