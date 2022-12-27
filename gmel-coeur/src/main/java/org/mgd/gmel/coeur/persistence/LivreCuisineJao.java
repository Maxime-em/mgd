package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.LivreCuisineDto;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;

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
    protected LivreCuisineDto to(LivreCuisine livreCuisine) {
        LivreCuisineDto livreCuisineDto = new LivreCuisineDto();
        livreCuisineDto.setNom(livreCuisine.getNom());
        livreCuisineDto.setRecettes(new RecetteJao().decharger(livreCuisine.getRecettes()));

        return livreCuisineDto;
    }

    @Override
    protected void copier(LivreCuisine source, LivreCuisine cible) throws JaoExecutionException {
        cible.setNom(source.getNom());
        cible.getRecettes().clear();
        cible.getRecettes().addAll(new RecetteJao().dupliquer(source.getRecettes()));
    }
}
