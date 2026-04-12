package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class TypeUnite extends Type implements Comparable<TypeUnite> {
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

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof TypeUnite type)) return false;
        return nom.equals(type.nom);
    }

    @Override
    public int compareTo(@NotNull TypeUnite type) {
        return Comparator.comparing(TypeUnite::getNom).compare(this, type);
    }
}
