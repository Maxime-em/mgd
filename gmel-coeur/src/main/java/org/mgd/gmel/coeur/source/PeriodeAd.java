package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.PeriodeDto;
import org.mgd.gmel.coeur.objet.Periode;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDate;

public class PeriodeAd extends Ad<PeriodeDto, Periode, PeriodeAf> {
    public PeriodeAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected PeriodeAf access(Path source) {
        return new PeriodeAf(source);
    }

    public Periode periode(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, MessageFormat.format("'{'\"repas\":\"{0}#PETIT_DEJEUNER\",\"taille\":1'}'", LocalDate.now())).jo();
    }
}
