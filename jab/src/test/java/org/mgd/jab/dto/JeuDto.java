package org.mgd.jab.dto;

import org.mgd.jab.persistence.JeuType;

public class JeuDto extends Dto {
    private JeuType type;
    private String nom;
    private Integer annee;
    private Integer semaine;
    private PegiDto pegi;

    public JeuType getType() {
        return type;
    }

    public void setType(JeuType type) {
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Integer getSemaine() {
        return semaine;
    }

    public void setSemaine(Integer semaine) {
        this.semaine = semaine;
    }

    public PegiDto getPegi() {
        return pegi;
    }

    public void setPegi(PegiDto pegi) {
        this.pegi = pegi;
    }
}
