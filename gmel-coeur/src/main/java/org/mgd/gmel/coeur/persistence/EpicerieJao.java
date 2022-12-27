package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.EpicerieDto;
import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;

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
    protected EpicerieDto to(Epicerie epicerie) {
        EpicerieDto epicerieDto = new EpicerieDto();
        epicerieDto.setProduits(new ProduitJao().decharger(epicerie.getProduits()));

        return epicerieDto;
    }

    @Override
    protected void copier(Epicerie source, Epicerie cible) throws JaoExecutionException {
        source.getProduits().clear();
        source.getProduits().addAll(new ProduitJao().dupliquer(cible.getProduits()));
    }
}
