package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

public abstract class TypeDto extends Dto {
    private Integer[] texture;
    private String nom;
    private String libelle;
    private Integer maximum;

    public Integer[] getTexture() {
        return texture;
    }

    public void setTexture(Integer[] texture) {
        this.texture = texture;
    }

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
