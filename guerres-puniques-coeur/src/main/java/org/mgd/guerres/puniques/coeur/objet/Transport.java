package org.mgd.guerres.puniques.coeur.objet;

public class Transport extends Tangible<TypeTransport> {
    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Transport transport)) return false;
        return transport.type.idem(transport.type);
    }
}
