package org.mgd.gmel.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;

import java.util.Comparator;

/**
 * Objet métier représentant un produit associé à une quantité qui feront partie d'une recette de cuisines.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class ProduitQuantifier extends Jo implements Comparable<ProduitQuantifier> {
    private final Joc<Produit> produit = new Joc<>(this);
    private final Joc<Quantite> quantite = new Joc<>(this);

    public Produit getProduit() {
        return produit.get();
    }

    public void setProduit(Produit produit) {
        this.produit.set(produit);
    }

    public Quantite getQuantite() {
        return quantite.get();
    }

    public void setQuantite(Quantite quantite) {
        this.quantite.set(quantite);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof ProduitQuantifier produitQuantifier)) return false;
        return produit.idem(produitQuantifier.produit) && quantite.idem(produitQuantifier.quantite);
    }

    @Override
    public int compareTo(ProduitQuantifier produitQuantifier) {
        return Comparator.comparing(ProduitQuantifier::getProduit).thenComparing(ProduitQuantifier::getQuantite).compare(this, produitQuantifier);
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
