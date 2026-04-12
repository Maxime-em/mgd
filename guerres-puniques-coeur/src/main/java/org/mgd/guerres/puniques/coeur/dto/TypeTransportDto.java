package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.List;

public class TypeTransportDto extends Dto {
    private List<TypeRegionDto> praticables;
    private Integer[] texture;
    private String nom;
    private String libelle;
    private Integer maximum;

    public List<TypeRegionDto> getPraticables() {
        return praticables;
    }

    public void setPraticables(List<TypeRegionDto> praticables) {
        this.praticables = praticables;
    }

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
