package org.mgd.jab.source;

import org.mgd.jab.dto.AdresseDto;
import org.mgd.jab.objet.Adresse;

import java.nio.file.Path;

public class AdresseAd extends Ad<AdresseDto, Adresse, AdresseAf> {
    public AdresseAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected AdresseAf access(Path source) {
        return new AdresseAf(source);
    }
}
