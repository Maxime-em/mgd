package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.InventaireDto;
import org.mgd.gmel.coeur.objet.Inventaire;
import org.mgd.gmel.coeur.persistence.InventaireJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class InventaireAf extends Af<InventaireDto, Inventaire> {
    protected InventaireAf(Path fichier) {
        super(new InventaireJao(), fichier);
    }
}
