package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.QuantiteDto;
import org.mgd.gmel.coeur.objet.Quantite;
import org.mgd.gmel.coeur.persistence.QuantiteJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class QuantiteAf extends Af<QuantiteDto, Quantite> {
    protected QuantiteAf(Path fichier) {
        super(new QuantiteJao(), fichier);
    }
}
