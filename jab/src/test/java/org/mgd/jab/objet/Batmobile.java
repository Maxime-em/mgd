package org.mgd.jab.objet;

/**
 * Véhicule de Batman, objet métier de test sans constructeur public sans argument.
 */
public class Batmobile extends Jo {
    private final String modele;

    public Batmobile(String modele) {
        super();
        this.modele = modele;
    }

    public String getModele() {
        return modele;
    }

    @Override
    public boolean idem(Object objet) {
        return objet instanceof Batmobile batmobile && modele.equals(batmobile.modele);
    }
}
