package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.ProduitQuantifierDto;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.gmel.coeur.persistence.ProduitQuantifierJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class ProduitQuantifierAf extends Af<ProduitQuantifierDto, ProduitQuantifier> {
    protected ProduitQuantifierAf(Path fichier) {
        super(new ProduitQuantifierJao(), fichier);
    }
}
