package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class Unite extends Jo implements Comparable<Unite> {
    private TypeUnite type;
    private Integer vie;

    public TypeUnite getType() {
        return type;
    }

    public void setType(TypeUnite type) {
        this.type = type;
    }

    public Integer getVie() {
        return vie;
    }

    public void setVie(Integer vie) {
        this.vie = vie;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Unite unite)) return false;
        return type == unite.type && Objects.equals(vie, unite.vie);
    }

    @Override
    public int compareTo(Unite unite) {
        return Comparator.comparing(Unite::getType).thenComparing(Unite::getVie).thenComparing(Unite::getIdentifiant).compare(this, unite);
    }
}
