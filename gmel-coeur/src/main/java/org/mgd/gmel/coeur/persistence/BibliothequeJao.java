package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.BibliothequeDto;
import org.mgd.gmel.coeur.objet.Bibliotheque;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;

/**
 * Classe permettant de manipuler les objets métiers de type {@link Bibliotheque} et de les charger depuis un système de
 * fichiers JSON via la classe de transfert {@link BibliothequeDto}.
 *
 * @author Maxime
 */
public class BibliothequeJao extends Jao<BibliothequeDto, Bibliotheque> {
    public BibliothequeJao() {
        super(BibliothequeDto.class, Bibliotheque.class);
    }

    @Override
    protected BibliothequeDto to(Bibliotheque bibliotheque) {
        BibliothequeDto bibliothequeDto = new BibliothequeDto();
        bibliothequeDto.setLivresCuisine(new LivreCuisineJao().decharger(bibliotheque.getLivresCuisine()));

        return bibliothequeDto;
    }

    @Override
    protected void copier(Bibliotheque source, Bibliotheque cible) throws JaoExecutionException {
        source.getLivresCuisine().clear();
        source.getLivresCuisine().addAll(new LivreCuisineJao().dupliquer(cible.getLivresCuisine()));
    }
}
