package org.mgd.gmel.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.JocTreeSet;

import java.util.SortedSet;

/**
 * Objet métier représentant un inventaire de produits quantifiés.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Inventaire extends Jo {
    private final SortedSet<ProduitQuantifier> produitsQuantifier = new JocTreeSet<>(this);

    public SortedSet<ProduitQuantifier> getProduitsQuantifier() {
        return produitsQuantifier;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Inventaire inventaire)) return false;
        return ((JocTreeSet<ProduitQuantifier>) produitsQuantifier).idem(inventaire.produitsQuantifier);
    }
}
