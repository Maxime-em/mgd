package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.FormuleDto;
import org.mgd.gmel.coeur.objet.Formule;
import org.mgd.gmel.coeur.persistence.FormuleJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class FormuleAf extends Af<FormuleDto, Formule> {
    protected FormuleAf(Path fichier) {
        super(new FormuleJao(), fichier);
    }
}
