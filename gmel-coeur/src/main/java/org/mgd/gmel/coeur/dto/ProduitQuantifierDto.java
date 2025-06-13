package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.gmel.coeur.persistence.EpicerieJao;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.dto.ReferenceDto;

/**
 * Classe de transformation vers l'objet métier {@link ProduitQuantifier} provenant d'un
 * système de fichiers JSON.
 *
 * @author Maxime
 */
public class ProduitQuantifierDto extends Dto {
    private ReferenceDto<EpicerieDto, Epicerie, EpicerieJao> produit;
    private QuantiteDto quantite;

    public ReferenceDto<EpicerieDto, Epicerie, EpicerieJao> getProduit() {
        return produit;
    }

    public void setProduit(ReferenceDto<EpicerieDto, Epicerie, EpicerieJao> produit) {
        this.produit = produit;
    }

    public QuantiteDto getQuantite() {
        return quantite;
    }

    public void setQuantite(QuantiteDto quantite) {
        this.quantite = quantite;
    }
}
