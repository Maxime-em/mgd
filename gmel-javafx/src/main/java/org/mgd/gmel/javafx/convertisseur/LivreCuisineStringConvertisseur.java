package org.mgd.gmel.javafx.convertisseur;

import javafx.beans.property.SimpleListProperty;
import javafx.util.StringConverter;
import org.mgd.gmel.coeur.objet.LivreCuisine;

public class LivreCuisineStringConvertisseur extends StringConverter<LivreCuisine> {
    private final SimpleListProperty<LivreCuisine> livresCuisine;

    public LivreCuisineStringConvertisseur(SimpleListProperty<LivreCuisine> livresCuisine) {
        this.livresCuisine = livresCuisine;
    }

    @Override
    public String toString(LivreCuisine livreCuisine) {
        return livreCuisine != null ? livreCuisine.getNom() : null;
    }

    @Override
    public LivreCuisine fromString(String nom) {
        return livresCuisine.stream().filter(livreCuisine -> livreCuisine.getNom().equals(nom)).findFirst().orElse(null);
    }
}
