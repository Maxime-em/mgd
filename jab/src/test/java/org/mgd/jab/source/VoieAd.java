package org.mgd.jab.source;

import org.mgd.jab.dto.VoieDto;
import org.mgd.jab.objet.Voie;

import java.nio.file.Path;

public class VoieAd extends Ad<VoieDto, Voie, VoieAf> {
    public VoieAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected VoieAf access(Path source) {
        return new VoieAf(source);
    }
}
