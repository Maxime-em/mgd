package org.mgd.jab.dto;

import java.util.Map;

public class LivreDto extends Dto {
    private String nom;
    private Map<ChapitreDto, PegiDto> chapitres;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Map<ChapitreDto, PegiDto> getChapitres() {
        return chapitres;
    }

    public void setChapitres(Map<ChapitreDto, PegiDto> chapitres) {
        this.chapitres = chapitres;
    }
}
