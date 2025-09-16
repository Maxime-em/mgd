package org.mgd.guerres.puniques.coeur.objet;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("java:S2160")
public class Armee extends Tangible<TypeArmee> {
    private final Set<Unite> unites = new TreeSet<>();
    private final Set<Des> desDegats = new HashSet<>();

    public Set<Unite> getUnites() {
        return unites;
    }

    public Set<Des> getDesDegats() {
        return desDegats;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Armee armee)) return false;
        return unites.equals(armee.unites);
    }
}
