package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.jab.dto.Dto;

import java.util.List;

/**
 * Classe de transformation vers l'objet métier {@link Epicerie} provenant d'un système de
 * fichiers JSON.
 *
 * @author Maxime
 */
public class EpicerieDto extends Dto {
    private List<ProduitDto> produits;

    public List<ProduitDto> getProduits() {
        return produits;
    }

    public void setProduits(List<ProduitDto> produits) {
        this.produits = produits;
    }
}
