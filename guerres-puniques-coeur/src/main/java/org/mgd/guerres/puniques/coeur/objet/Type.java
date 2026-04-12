package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;

@SuppressWarnings("java:S2160")
public abstract class Type extends Jo {
    protected String nom;
    protected String libelle;
    protected Integer maximum;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }
}
