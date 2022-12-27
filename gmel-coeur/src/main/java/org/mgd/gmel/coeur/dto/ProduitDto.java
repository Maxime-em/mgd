package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.jab.dto.Dto;

/**
 * Classe de transformation vers l'objet métier {@link Produit} provenant d'un système de
 * fichiers JSON.
 *
 * @author Maxime
 */
public class ProduitDto extends Dto {
    private String nom;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
