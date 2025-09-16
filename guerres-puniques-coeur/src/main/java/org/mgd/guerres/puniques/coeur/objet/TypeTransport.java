package org.mgd.guerres.puniques.coeur.objet;

import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("java:S2160")
public class TypeTransport extends Type {
    private final Set<TypeRegion> praticables = new TreeSet<>();

    public Set<TypeRegion> getPraticables() {
        return praticables;
    }
}
