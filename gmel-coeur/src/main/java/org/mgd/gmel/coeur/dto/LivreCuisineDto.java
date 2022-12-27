package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.jab.dto.Dto;

import java.util.List;

/**
 * Classe de transformation vers l'objet métier {@link LivreCuisine} provenant d'un système de
 * fichiers JSON.
 *
 * @author Maxime
 */
public class LivreCuisineDto extends Dto {
    private String nom;

    private List<RecetteDto> recettes;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<RecetteDto> getRecettes() {
        return recettes;
    }

    public void setRecettes(List<RecetteDto> recettes) {
        this.recettes = recettes;
    }
}
