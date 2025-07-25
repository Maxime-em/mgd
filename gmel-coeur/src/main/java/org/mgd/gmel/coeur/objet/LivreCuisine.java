package org.mgd.gmel.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;
import org.mgd.jab.objet.JocTreeSet;

import java.util.SortedSet;

/**
 * Objet métier représentant un livre de cuisine.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class LivreCuisine extends Jo implements Comparable<LivreCuisine> {
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
