package org.mgd.jab;

import org.mgd.jab.exception.JabException;

import java.nio.file.Path;

public class Jabt extends Jab {
    protected Jabt(Path chemin) throws JabException {
        super(chemin);
    }
}
