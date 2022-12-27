package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.jab.dto.Dto;

import java.util.List;

/**
 * Classe de transformation vers l'objet métier {@link Recette} provenant d'un système de
 * fichiers JSON.
 *
 * @author Maxime
 */
public class RecetteDto extends Dto {
    private String nom;
    private Integer nombrePersonnes;
    private List<ProduitQuantifierDto> produitsQuantifier;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(Integer nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public List<ProduitQuantifierDto> getProduitsQuantifier() {
        return produitsQuantifier;
    }

    public void setProduitsQuantifier(List<ProduitQuantifierDto> produitsQuantifier) {
        this.produitsQuantifier = produitsQuantifier;
    }
}
