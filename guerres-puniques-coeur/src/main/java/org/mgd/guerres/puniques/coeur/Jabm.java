package org.mgd.guerres.puniques.coeur;

import org.mgd.jab.Jab;
import org.mgd.jab.exception.JabException;

import java.nio.file.Path;

public class Jabm extends Jab {
    protected Jabm(Path chemin) throws JabException {
        super(chemin);
    }
}
