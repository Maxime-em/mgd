package org.mgd.guerres.puniques.coeur.source;

import org.mgd.guerres.puniques.coeur.dto.PartieDto;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.persistence.PartieJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class PartieAf extends Af<PartieDto, Partie> {
    protected PartieAf(Path fichier) {
        super(new PartieJao(), fichier);
    }
}
