package org.mgd.guerres.puniques.coeur.source;

import org.mgd.guerres.puniques.coeur.dto.RegistreDto;
import org.mgd.guerres.puniques.coeur.objet.Registre;
import org.mgd.guerres.puniques.coeur.persistence.RegistreJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class RegistreAf extends Af<RegistreDto, Registre> {
    protected RegistreAf(Path fichier) {
        super(new RegistreJao(), fichier);
    }
}
