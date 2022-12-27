package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.QuantiteDto;
import org.mgd.gmel.coeur.objet.Quantite;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;

public class QuantiteAd extends Ad<QuantiteDto, Quantite, QuantiteAf> {
    public QuantiteAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected QuantiteAf access(Path source) {
        return new QuantiteAf(source);
    }

    public Quantite quantite(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, "{\"valeur\":1,\"mesure\":\"MASSE\"}").jo();
    }
}
