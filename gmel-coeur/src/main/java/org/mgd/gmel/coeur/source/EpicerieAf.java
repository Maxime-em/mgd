package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.EpicerieDto;
import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.gmel.coeur.persistence.EpicerieJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class EpicerieAf extends Af<EpicerieDto, Epicerie> {
    protected EpicerieAf(Path fichier) {
        super(new EpicerieJao(), fichier);
    }
}
