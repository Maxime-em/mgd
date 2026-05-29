package org.mgd.guerres.puniques.coeur.objet;

import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("java:S2160")
public class TypeUnite extends Type {
    private final Set<TypeRegion> praticables = new TreeSet<>();
    private Integer constitution;
    private Integer force;

    public Set<TypeRegion> getPraticables() {
        return praticables;
    }

    public Integer getConstitution() {
        return constitution;
    }

    public void setConstitution(Integer constitution) {
        this.constitution = constitution;
    }

    public Integer getForce() {
        return force;
    }

    public void setForce(Integer force) {
        this.force = force;
    }
}
