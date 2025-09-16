package org.mgd.guerres.puniques.coeur.source;

import org.mgd.guerres.puniques.coeur.dto.PartieDto;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.jab.source.Ad;

import java.nio.file.Path;

public class PartieAd extends Ad<PartieDto, Partie, PartieAf> {
    public PartieAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected PartieAf access(Path source) {
        return new PartieAf(source);
    }
}
