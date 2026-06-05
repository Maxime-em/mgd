package org.mgd.guerres.puniques.coeur.objet;

import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("java:S2160")
public class Transport extends Tangible<TypeTransport> {
    private final Set<Armee> armees = new TreeSet<>();

    public Set<Armee> getArmees() {
        return armees;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Transport transport)) return false;
        return transport.type.idem(transport.type);
    }
}
