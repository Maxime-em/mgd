package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.RecetteDto;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;

/**
 * Classe permettant de manipuler les objets métiers de type {@link Recette} et de les charger depuis un système de
 * fichiers JSON via la classe de transfert {@link RecetteDto}.
 *
 * @author Maxime
 */
public class RecetteJao extends Jao<RecetteDto, Recette> {
    public RecetteJao() {
        super(RecetteDto.class, Recette.class);
    }

    @Override
    protected RecetteDto to(Recette recette) {
        RecetteDto recetteDto = new RecetteDto();
        recetteDto.setNom(recette.getNom());
        recetteDto.setNombrePersonnes(recette.getNombrePersonnes());
        recetteDto.setProduitsQuantifier(new ProduitQuantifierJao().decharger(recette.getProduitsQuantifier()));

        return recetteDto;
    }

    @Override
    protected void copier(Recette source, Recette cible) throws JaoExecutionException {
        cible.setNom(source.getNom());
        cible.setNombrePersonnes(source.getNombrePersonnes());
        cible.getProduitsQuantifier().clear();
        cible.getProduitsQuantifier().addAll(new ProduitQuantifierJao().dupliquer(source.getProduitsQuantifier()));
    }
}
