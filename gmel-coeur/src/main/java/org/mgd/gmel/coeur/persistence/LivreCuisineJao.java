package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.LivreCuisineDto;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

/**
 * Classe permettant de manipuler les objets métiers de type {@link LivreCuisine} et de les charger depuis un système de
 * fichiers JSON via la classe de transfert {@link LivreCuisineDto}.
 *
 * @author Maxime
 */
public class LivreCuisineJao extends Jao<LivreCuisineDto, LivreCuisine> {
    public LivreCuisineJao() {
        super(LivreCuisineDto.class, LivreCuisine.class);
    }

    @Override
    public LivreCuisineDto dto(LivreCuisine livreCuisine) {
        LivreCuisineDto livreCuisineDto = new LivreCuisineDto();
        livreCuisineDto.setNom(livreCuisine.getNom());
        livreCuisineDto.setRecettes(new RecetteJao().decharger(livreCuisine.getRecettes()));

        return livreCuisineDto;
    }

    @Override
    public void enrichir(LivreCuisineDto dto, LivreCuisine livreCuisine) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonVide(dto.getNom(), "Le nom de livre de cuisine \"{0}\" est incorrect");
        Verifications.nonNull(dto.getRecettes(), "Les recettes d''un livre de cuisine devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getRecettes(),
                (recetteDto, recetteDto2) -> recetteDto.getNom().equals(recetteDto2.getNom()),
                "Les noms des recettes dans un livre de cuisine doivent être unique"
        );

        livreCuisine.setNom(dto.getNom());
        livreCuisine.getRecettes().addAll(new RecetteJao().charger(dto.getRecettes(), livreCuisine));
    }

    @Override
    protected void copier(LivreCuisine source, LivreCuisine cible) throws JaoExecutionException, JaoParseException {
        cible.setNom(source.getNom());
        cible.getRecettes().clear();
        cible.getRecettes().addAll(new RecetteJao().dupliquer(source.getRecettes()));
    }
}
