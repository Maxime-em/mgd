package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.JocTreeSet;

import java.util.SortedSet;

@SuppressWarnings("java:S2160")
public class Partie extends Jo {
    private final SortedSet<Civilisation> civilisations = new JocTreeSet<>(this);
    private Informations informations;
    private Monde monde;
    private Des desCivilisation;
    private Des desActions;

    public SortedSet<Civilisation> getCivilisations() {
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
        return ((JocTreeSet<Civilisation>) civilisations).idem(partie.civilisations)
                && informations.idem(partie.informations)
                && monde.idem(partie.monde)
                && desCivilisation.idem(partie.desCivilisation)
                && desActions.idem(partie.desActions);
    }
}
