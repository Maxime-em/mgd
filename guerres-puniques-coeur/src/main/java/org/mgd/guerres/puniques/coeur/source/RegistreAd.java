package org.mgd.guerres.puniques.coeur.source;

import org.mgd.guerres.puniques.coeur.dto.RegistreDto;
import org.mgd.guerres.puniques.coeur.objet.Registre;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;

public class RegistreAd extends Ad<RegistreDto, Registre, RegistreAf> {
    public RegistreAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected RegistreAf access(Path source) {
        return new RegistreAf(source);
    }

    public Registre registre(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, "{\"informations\":{}}").jo();
    }
}
