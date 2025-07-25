package org.mgd.jab.objet;

import java.util.Set;

public class Monde extends Jo {
    private final Set<Pays> payss = new JocTreeSet<>(this);

    public Set<Pays> getPayss() {
        return payss;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Monde monde)) return false;
        return ((JocTreeSet<Pays>) payss).idem(monde.payss);
    }
}
