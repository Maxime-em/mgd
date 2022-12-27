package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.FormuleDto;
import org.mgd.gmel.coeur.objet.Formule;
import org.mgd.jab.source.Ad;

import java.nio.file.Path;

public class FormuleAd extends Ad<FormuleDto, Formule, FormuleAf> {
    public FormuleAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected FormuleAf access(Path source) {
        return new FormuleAf(source);
    }
}
