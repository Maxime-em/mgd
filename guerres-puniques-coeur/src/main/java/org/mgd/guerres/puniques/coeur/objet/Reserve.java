package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;

import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("java:S2160")
public class Reserve extends Jo {
    private final Set<Unite> unites = new TreeSet<>();

    public Set<Unite> getUnites() {
        return unites;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Reserve reserve)) return false;
        return unites.equals(reserve.unites);
    }
}
