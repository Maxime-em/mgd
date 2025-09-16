package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;

public class Partie extends Jo {
    private Monde monde;

    public Monde getMonde() {
        return monde;
    }

    public void setMonde(Monde monde) {
        this.monde = monde;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Partie partie)) return false;
        return monde.idem(partie.monde);
    }
}
