package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.RecetteDto;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

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
    public RecetteDto dto(Recette recette) {
        RecetteDto recetteDto = new RecetteDto();
        recetteDto.setNom(recette.getNom());
        recetteDto.setNombrePersonnes(recette.getNombrePersonnes());
        recetteDto.setProduitsQuantifier(new ProduitQuantifierJao().decharger(recette.getProduitsQuantifier()));

        return recetteDto;
    }

    @Override
    public void enrichir(RecetteDto dto, Recette recette) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonVide(dto.getNom(), "Le nom de recette \"{0}\" est incorrect");
        Verifications.nonNull(dto.getProduitsQuantifier(), "Les produits quantifiés d''une recette devrait être une liste éventuellement vide");
        Verifications.nonNegatif(dto.getNombrePersonnes(), "Le nombre de personne d''une recette doit être un entier strictement positif");

        recette.setNom(dto.getNom());
        recette.setNombrePersonnes(dto.getNombrePersonnes());
        recette.getProduitsQuantifier().addAll(new ProduitQuantifierJao().charger(dto.getProduitsQuantifier(), recette));
    }

    @Override
    protected void copier(Recette source, Recette cible) throws JaoExecutionException, JaoParseException {
        cible.setNom(source.getNom());
        cible.setNombrePersonnes(source.getNombrePersonnes());
        cible.getProduitsQuantifier().clear();
        cible.getProduitsQuantifier().addAll(new ProduitQuantifierJao().dupliquer(source.getProduitsQuantifier()));
    }
}
