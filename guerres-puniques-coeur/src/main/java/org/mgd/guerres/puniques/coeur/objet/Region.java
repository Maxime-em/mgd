package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.guerres.puniques.coeur.commun.Alignement;
import org.mgd.guerres.puniques.coeur.commun.TypeRegion;
import org.mgd.jab.objet.Jo;

import java.util.Set;
import java.util.TreeSet;

public class Region extends Jo {
    private final Set<TypeRegion> types = new TreeSet<>();
    private final Set<Armee> armees = new TreeSet<>();
    private Alignement alignement;

    public Set<TypeRegion> getTypes() {
        return types;
    }

    public Set<Armee> getArmees() {
        return armees;
    }

    public Alignement getAlignement() {
        return alignement;
    }

    public void setAlignement(Alignement alignement) {
        this.alignement = alignement;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Region region)) return false;
        return alignement == region.alignement && types.equals(region.types) && (armees).equals(region.armees);
    }
}
