package org.mgd.jab.objet;

import java.util.Set;

public class Communaute extends Jo {
    private final Set<Commune> communes = new JocTreeSet<>(this);

    public Set<Commune> getCommunes() {
        return communes;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Communaute communaute)) return false;
        return ((JocTreeSet<Commune>) communes).idem(communaute.communes);
    }
}
