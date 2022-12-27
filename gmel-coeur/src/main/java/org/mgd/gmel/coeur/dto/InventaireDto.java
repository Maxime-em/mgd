package org.mgd.gmel.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.List;

public class InventaireDto extends Dto {
    private List<ProduitQuantifierDto> produitsQuantifier;

    public List<ProduitQuantifierDto> getProduitsQuantifier() {
        return produitsQuantifier;
    }

    public void setProduitsQuantifier(List<ProduitQuantifierDto> produitsQuantifier) {
        this.produitsQuantifier = produitsQuantifier;
    }
}
