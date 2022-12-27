package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.jab.dto.Dto;

/**
 * Classe de transformation vers l'objet métier {@link ProduitQuantifier} provenant d'un
 * système de fichiers JSON.
 *
 * @author Maxime
 */
public class ProduitQuantifierDto extends Dto {
    private ProduitDto produit;
    private QuantiteDto quantite;

    public ProduitDto getProduit() {
        return produit;
    }

    public void setProduit(ProduitDto produit) {
        this.produit = produit;
    }

    public QuantiteDto getQuantite() {
        return quantite;
    }

    public void setQuantite(QuantiteDto quantite) {
        this.quantite = quantite;
    }
}
