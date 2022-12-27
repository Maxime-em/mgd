package org.mgd.gmel.javafx.convertisseur;

import javafx.beans.property.SimpleListProperty;
import javafx.util.StringConverter;
import org.mgd.gmel.coeur.objet.Produit;

public class ProduitStringConvertisseur extends StringConverter<Produit> {
    private final SimpleListProperty<Produit> produits;

    public ProduitStringConvertisseur(SimpleListProperty<Produit> produits) {
        this.produits = produits;
    }

    @Override
    public String toString(Produit produit) {
        return produit != null ? produit.getNom() : null;
    }

    @Override
    public Produit fromString(String nom) {
        return produits.stream().filter(produit -> produit.getNom().equals(nom)).findFirst().orElse(null);
    }
}
