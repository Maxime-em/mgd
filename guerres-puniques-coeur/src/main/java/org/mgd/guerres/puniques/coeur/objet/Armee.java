package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.guerres.puniques.coeur.commun.Alignement;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Armee extends Jo implements Comparable<Armee> {
    private final Set<Unite> unites = new TreeSet<>();
    private Alignement alignement;

    public Set<Unite> getUnites() {
        return unites;
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
        if (!(objet instanceof Armee armee)) return false;
        return alignement == armee.alignement && unites.equals(armee.unites);
    }

    @Override
    public int compareTo(Armee armee) {
        return Comparator.comparing(Armee::getAlignement).compare(this, armee);
    }
}
