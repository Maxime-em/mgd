package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;

@SuppressWarnings({"java:S2160", "java:S1210"})
public abstract class Tangible<T extends Type> extends Jo implements Comparable<Tangible<T>> {
    protected T type;
    protected Civilisation origine;

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public Civilisation getOrigine() {
        return origine;
    }

    public void setOrigine(Civilisation origine) {
        this.origine = origine;
    }

    @Override
    public int compareTo(@NotNull Tangible<T> tangible) {
        return Comparator.comparing(Jo::getIdentifiant).compare(this, tangible);
    }
}
