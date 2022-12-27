package org.mgd.gmel.coeur.commun;

import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.gmel.coeur.objet.Quantite;

/**
 * Enumération des mesures utilisées dans la classe {@link Quantite} pour quantifier des
 * produits de type {@link Produit}.
 *
 * @author Maxime
 */
public enum Mesure {
    VOLUME("litre", "millilitres"),
    MASSE("gramme", "grammes"),
    UNITE("unité", "unités");

    private final String uniteSingulier;
    private final String unitePluriel;

    Mesure(String uniteSingulier, String unitePluriel) {
        this.uniteSingulier = uniteSingulier;
        this.unitePluriel = unitePluriel;
    }

    public String getUnite(boolean singulier) {
        return singulier ? uniteSingulier : unitePluriel;
    }
}
