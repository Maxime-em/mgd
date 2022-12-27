package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.EpicerieDto;
import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;

public class EpicerieAd extends Ad<EpicerieDto, Epicerie, EpicerieAf> {
    public EpicerieAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected EpicerieAf access(Path source) {
        return new EpicerieAf(source);
    }

    public Epicerie epicerie(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, "{\"produits\":[]}").jo();
    }
}
