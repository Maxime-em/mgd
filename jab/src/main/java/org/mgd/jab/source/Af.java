package org.mgd.jab.source;

import org.mgd.jab.dto.Dto;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Les instances enfants permettent de lier un objet métier {@link Jo} à son fichier source.
 *
 * @param <D> Type de l'objet de transfert {@link Dto}
 * @param <O> Type de l'objet métier {@link Jo}
 * @author Maxime
 */
public abstract class Af<D extends Dto, O extends Jo> {
    private final Jao<D, O> jao;
    private final Path fichier;
    protected O jo;

    protected Af(Jao<D, O> jao, Path fichier) {
        this.jao = jao;
        this.fichier = fichier;
    }

    public O jo() throws JaoParseException, JaoExecutionException {
        if (Objects.isNull(jo)) {
            jo = jao.charger(fichier);
        }
        return jo;
    }
}
