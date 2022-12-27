package org.mgd.jab.source;

import org.mgd.jab.dto.AdresseDto;
import org.mgd.jab.objet.Adresse;
import org.mgd.jab.persistence.AdresseJao;

import java.nio.file.Path;

public class AdresseAf extends Af<AdresseDto, Adresse> {
    protected AdresseAf(Path source) {
        super(new AdresseJao(), source);
    }
}
