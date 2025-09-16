package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("java:S2160")
public class Registre extends Jo {
    private final Map<UUID, Informations> informations = new HashMap<>();

    public Map<UUID, Informations> getInformations() {
        return informations;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Registre registre)) return false;
        return informations.equals(registre.informations);
    }
}
