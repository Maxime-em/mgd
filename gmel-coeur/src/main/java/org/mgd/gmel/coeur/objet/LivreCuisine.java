package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.dto.LivreCuisineDto;
import org.mgd.gmel.coeur.persistence.LivreCuisineJao;
import org.mgd.gmel.coeur.persistence.RecetteJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;
import org.mgd.jab.objet.JocTreeSet;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.SortedSet;

/**
 * Objet métier représentant un livre de cuisine.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class LivreCuisine extends Jo<LivreCuisineDto> implements Comparable<LivreCuisine> {
    private final SortedSet<Recette> recettes = new JocTreeSet<>(this);
    private final Joc<String> nom = new Joc<>(this);

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public SortedSet<Recette> getRecettes() {
        return recettes;
    }

    @Override
    public LivreCuisineDto dto() {
        return new LivreCuisineJao().decharger(this);
    }

    @Override
    public void depuis(LivreCuisineDto dto) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonVide(dto.getNom(), "Le nom de livre de cuisine \"{0}\" est incorrect");
        Verifications.nonNull(dto.getRecettes(), "Les recettes d''un livre de cuisine devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getRecettes(),
                (recetteDto, recetteDto2) -> recetteDto.getNom().equals(recetteDto2.getNom()),
                "Les noms des recettes dans un livre de cuisine doivent être unique"
        );

        setNom(dto.getNom());
        getRecettes().addAll(new RecetteJao().charger(dto.getRecettes(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof LivreCuisine livreCuisine)) return false;
        return nom.idem(livreCuisine.nom) && ((JocTreeSet<Recette>) recettes).idem(livreCuisine.recettes);
    }

    @Override
    public int compareTo(LivreCuisine livreCuisine) {
        return nom.get().compareToIgnoreCase(livreCuisine.nom.get());
    }

    @Override
    public boolean equals(Object objet) {
        return super.equals(objet);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
