package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.guerres.puniques.coeur.commun.TypeRegion;
import org.mgd.jab.objet.Jo;

import java.text.MessageFormat;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("java:S2160")
public class Region extends Jo {
    private final Set<Alignement> alignements = new TreeSet<>();
    private final Set<TypeRegion> types = new TreeSet<>();
    private final Set<Armee> armees = new TreeSet<>();

    public Set<Alignement> getAlignements() {
        return alignements;
    }

    public Set<TypeRegion> getTypes() {
        return types;
    }

    public Set<Armee> getArmees() {
        return armees;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Region region)) return false;
        return alignements.equals(region.alignements) && types.equals(region.types) && (armees).equals(region.armees);
    }

    public String getInformations() {
        return MessageFormat.format("Types {0}", types);
    }
}
