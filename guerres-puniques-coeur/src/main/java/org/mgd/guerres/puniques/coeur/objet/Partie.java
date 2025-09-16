package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("java:S2160")
public class Partie extends Jo {
    private final Set<Civilisation> civilisations = new HashSet<>();
    private Informations informations;
    private Monde monde;

    public Informations getInformations() {
        return informations;
    }

    public void setInformations(Informations informations) {
        this.informations = informations;
    }

    public Monde getMonde() {
        return monde;
    }

    public void setMonde(Monde monde) {
        this.monde = monde;
    }

    public Set<Civilisation> getCivilisations() {
        return civilisations;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Partie partie)) return false;
        return informations.idem(partie.informations) && monde.idem(partie.monde);
    }
}
