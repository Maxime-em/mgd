package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.PeriodeDto;
import org.mgd.gmel.coeur.objet.Periode;
import org.mgd.gmel.coeur.persistence.PeriodeJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class PeriodeAf extends Af<PeriodeDto, Periode> {
    protected PeriodeAf(Path fichier) {
        super(new PeriodeJao(), fichier);
    }
}
