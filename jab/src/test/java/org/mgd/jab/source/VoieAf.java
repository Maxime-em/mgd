package org.mgd.jab.source;

import org.mgd.jab.dto.VoieDto;
import org.mgd.jab.objet.Voie;
import org.mgd.jab.persistence.VoieJao;

import java.nio.file.Path;

public class VoieAf extends Af<VoieDto, Voie> {
    protected VoieAf(Path source) {
        super(new VoieJao(), source);
    }
}
