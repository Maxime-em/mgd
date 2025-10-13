package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.InventaireDto;
import org.mgd.gmel.coeur.objet.Inventaire;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

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
    public InventaireDto dto(Inventaire inventaire) {
        InventaireDto inventaireDto = new InventaireDto();
        inventaireDto.setProduitsQuantifier(new ProduitQuantifierJao().decharger(inventaire.getProduitsQuantifier()));

        return inventaireDto;
    }

    @Override
    public void enrichir(InventaireDto dto, Inventaire inventaire) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getProduitsQuantifier(), "Les produits quantifiés d''une liste de courses devrait être une liste éventuellement vide");

        inventaire.getProduitsQuantifier().addAll(new ProduitQuantifierJao().charger(dto.getProduitsQuantifier(), inventaire));
    }

    @Override
    protected void copier(Inventaire source, Inventaire cible) throws JaoExecutionException, JaoParseException {
        cible.getProduitsQuantifier().clear();
        cible.getProduitsQuantifier().addAll(new ProduitQuantifierJao().dupliquer(source.getProduitsQuantifier()));
    }
}
