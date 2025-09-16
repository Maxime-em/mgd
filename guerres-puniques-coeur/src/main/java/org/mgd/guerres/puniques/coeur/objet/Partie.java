package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("java:S2160")
public class Partie extends Jo {
    private final Set<Civilisation> civilisations = new HashSet<>();
    private Informations informations;
    private Monde monde;
    private Des desCivilisation;
    private Des desActions;

    public Set<Civilisation> getCivilisations() {
        return civilisations;
    }

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

    public Des getDesCivilisation() {
        return desCivilisation;
    }

    public void setDesCivilisation(Des desCivilisation) {
        this.desCivilisation = desCivilisation;
    }

    public Des getDesActions() {
        return desActions;
    }

    public void setDesActions(Des desActions) {
        this.desActions = desActions;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Partie partie)) return false;
        return civilisations.equals(partie.civilisations) // TODO il faudrait appeler idem
                && informations.idem(partie.informations)
                && monde.idem(partie.monde)
                && desCivilisation.idem(partie.desCivilisation)
                && desActions.idem(partie.desActions);
    }
}
