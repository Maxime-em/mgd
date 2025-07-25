package org.mgd.gmel.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.JocTreeSet;

import java.util.SortedSet;

/**
 * Objet métier représentant une bibliothèque de livres de cuisine.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Bibliotheque extends Jo {
    private final SortedSet<LivreCuisine> livresCuisine = new JocTreeSet<>(this);

    public SortedSet<LivreCuisine> getLivresCuisine() {
        return livresCuisine;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Bibliotheque bibliotheque)) return false;
        return ((JocTreeSet<LivreCuisine>) livresCuisine).idem(bibliotheque.livresCuisine);
    }
}
