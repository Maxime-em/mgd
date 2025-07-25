package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.BibliothequeDto;
import org.mgd.gmel.coeur.objet.Bibliotheque;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

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
    public BibliothequeDto dto(Bibliotheque bibliotheque) {
        BibliothequeDto bibliothequeDto = new BibliothequeDto();
        bibliothequeDto.setLivresCuisine(new LivreCuisineJao().decharger(bibliotheque.getLivresCuisine()));

        return bibliothequeDto;
    }

    @Override
    public void enrichir(BibliothequeDto dto, Bibliotheque bibliotheque) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getLivresCuisine(), "Les livres de cuisine d''une bibliothèques devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getLivresCuisine(),
                (livreCuisineDto1, livreCuisineDto2) -> livreCuisineDto1.getNom().equals(livreCuisineDto2.getNom()),
                "Les noms des livres de cuisines dans un bibliothèque doivent être unique"
        );
        bibliotheque.getLivresCuisine().addAll(new LivreCuisineJao().charger(dto.getLivresCuisine(), bibliotheque));
    }

    @Override
    protected void copier(Bibliotheque source, Bibliotheque cible) throws JaoExecutionException, JaoParseException {
        source.getLivresCuisine().clear();
        source.getLivresCuisine().addAll(new LivreCuisineJao().dupliquer(cible.getLivresCuisine()));
    }
}
