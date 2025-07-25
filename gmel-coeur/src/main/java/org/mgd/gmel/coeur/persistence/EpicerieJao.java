package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.EpicerieDto;
import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

/**
 * Classe permettant de manipuler les objets métiers de type {@link Epicerie} et de les charger depuis un système de
 * fichiers JSON via la classe de transfert {@link EpicerieDto}.
 *
 * @author Maxime
 */
public class EpicerieJao extends Jao<EpicerieDto, Epicerie> {
    public EpicerieJao() {
        super(EpicerieDto.class, Epicerie.class);
    }

    @Override
    public EpicerieDto dto(Epicerie epicerie) {
        EpicerieDto epicerieDto = new EpicerieDto();
        epicerieDto.setProduits(new ProduitJao().decharger(epicerie.getProduits()));

        return epicerieDto;
    }

    @Override
    public void enrichir(EpicerieDto dto, Epicerie epicerie) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getProduits(), "Les produits d''une épicerie devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getProduits(),
                (produitDto1, produitDto2) -> produitDto1.getNom().equals(produitDto2.getNom()),
                "Les noms des produits dans une épicerie doivent être unique"
        );
        epicerie.getProduits().addAll(new ProduitJao().charger(dto.getProduits(), epicerie));
    }

    @Override
    protected void copier(Epicerie source, Epicerie cible) throws JaoExecutionException, JaoParseException {
        source.getProduits().clear();
        source.getProduits().addAll(new ProduitJao().dupliquer(cible.getProduits()));
    }
}
