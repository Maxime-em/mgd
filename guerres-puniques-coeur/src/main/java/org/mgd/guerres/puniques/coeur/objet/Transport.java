package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class Transport extends Jo implements Comparable<Transport>, Typable {
    private TypeTransport type;

    @Override
    public TypeTransport getType() {
        return type;
    }

    public void setType(TypeTransport type) {
        this.type = type;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Transport transport)) return false;
        return transport.idem(transport.type);
    }

    @Override
    public int compareTo(@NotNull Transport transport) {
        return Comparator.comparing(Transport::getType).thenComparing(Transport::getIdentifiant).compare(this, transport);
    }
}
