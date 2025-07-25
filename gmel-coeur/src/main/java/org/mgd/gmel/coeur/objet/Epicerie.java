package org.mgd.gmel.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.JocTreeSet;

import java.util.SortedSet;

/**
 * Objet métier représentant une épicerie qui met à disposition des produits.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Epicerie extends Jo {
    private final SortedSet<Produit> produits = new JocTreeSet<>(this);

    public SortedSet<Produit> getProduits() {
        return produits;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Epicerie epicerie)) return false;
        return ((JocTreeSet<Produit>) produits).idem(epicerie.produits);
    }
}
