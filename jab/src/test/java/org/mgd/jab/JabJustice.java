package org.mgd.jab;

import org.mgd.jab.dto.Dto;
import org.mgd.jab.exception.JabException;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.source.Ad;
import org.mgd.jab.source.Af;

import java.nio.file.Path;
import java.util.Properties;
import java.util.SortedMap;

/**
 * Jab de la Ligue de Justice, expose les éléments internes pour les tests.
 */
public class JabJustice extends Jab {
    public JabJustice(Path chemin) throws JabException {
        super(chemin);
    }

    public SortedMap<String, Jao<? extends Dto, ? extends Jo>> jaos() {
        return jaos;
    }

    public SortedMap<String, Ad<? extends Dto, ? extends Jo, ? extends Af<? extends Dto, ? extends Jo>>> ads() {
        return ads;
    }

    public Path base() {
        return base;
    }

    public Properties proprietes() {
        return proprietes;
    }
}
