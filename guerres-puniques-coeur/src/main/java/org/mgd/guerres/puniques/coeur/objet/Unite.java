package org.mgd.guerres.puniques.coeur.objet;

import java.util.Objects;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class Unite extends Tangible<TypeUnite> {
    private Integer vie;

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
}
