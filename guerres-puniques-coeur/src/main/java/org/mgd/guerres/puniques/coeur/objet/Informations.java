package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;

@SuppressWarnings("java:S2160")
public class Informations extends Jo {
    private String nom;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Informations informations)) return false;
        return nom.equals(informations.nom);
    }
}
