package org.mgd.gmel.coeur.commun;

import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.gmel.coeur.objet.Quantite;

import java.util.Arrays;

/**
 * Enumération des mesures utilisées dans la classe {@link Quantite} pour quantifier des
 * produits de type {@link Produit}.
 *
 * @author Maxime
 */
public enum Mesure {
    VOLUME("Volume", "litre", "millilitres"),
    MASSE("Masse", "gramme", "grammes"),
    UNITE("Unité", "unité", "unités"),
    BOITE("Boîte", "boîte", "boîtes"),
    BRIQUE("Brique", "brique", "briques"),
    CONSERVE("Conserve", "conserve", "conserves"),
    POT("Pot", "pot", "pots"),
    SACHET("Sachet", "sachet", "sachets"),
    TRANCHE("Tranche", "tranche", "tranches"),
    BLOQUE("Bloque", "bloque", "bloques"),
    PORTION("Portion", "portion", "portions"),
    BARQUETTE("Barquette", "barquette", "barquettes"),
    CUILLERE("Cuillère", "cuillère", "cuillères"),
    CUILLERE_A_SOUPE("Cuillère à soupe", "cuillère à soupe", "cuillères à soupe");

    private final String nom;
    private final String uniteSingulier;
    private final String unitePluriel;

    Mesure(String nom, String uniteSingulier, String unitePluriel) {
        this.nom = nom;
        this.uniteSingulier = uniteSingulier;
        this.unitePluriel = unitePluriel;
    }

    public String getUnite(boolean singulier) {
        return singulier ? uniteSingulier : unitePluriel;
    }

    public static Mesure depuisNom(String nom) {
        return Arrays.stream(Mesure.values()).filter(mesure -> mesure.getNom().equals(nom)).findFirst().orElseThrow();
    }

    public String getNom() {
        return nom;
    }
}
