package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.InventaireDto;
import org.mgd.gmel.coeur.objet.Inventaire;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;

/**
 * Classe permettant de manipuler les objets métiers de type {@link Inventaire} et de les charger depuis un système de
 * fichiers JSON via la classe de transfert {@link InventaireDto}.
 *
 * @author Maxime
 */
public class InventaireJao extends Jao<InventaireDto, Inventaire> {
    public InventaireJao() {
        super(InventaireDto.class, Inventaire.class);
    }

    @Override
    protected InventaireDto to(Inventaire inventaire) {
        InventaireDto inventaireDto = new InventaireDto();
        inventaireDto.setProduitsQuantifier(new ProduitQuantifierJao().decharger(inventaire.getProduitsQuantifier()));

        return inventaireDto;
    }

    @Override
    protected void copier(Inventaire source, Inventaire cible) throws JaoExecutionException {
        source.getProduitsQuantifier().clear();
        source.getProduitsQuantifier().addAll(new ProduitQuantifierJao().dupliquer(cible.getProduitsQuantifier()));
    }
}
