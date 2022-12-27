package org.mgd.gmel.javafx.convertisseur;

import javafx.beans.property.SimpleListProperty;
import javafx.util.StringConverter;
import org.mgd.gmel.coeur.objet.Recette;

public class RecetteStringConvertisseur extends StringConverter<Recette> {
    private final SimpleListProperty<Recette> recettes;

    public RecetteStringConvertisseur(SimpleListProperty<Recette> recettes) {
        this.recettes = recettes;
    }

    @Override
    public String toString(Recette recette) {
        return recette != null ? recette.getNom() : null;
    }

    @Override
    public Recette fromString(String nom) {
        return recettes.stream().filter(recette -> recette.getNom().equals(nom)).findFirst().orElse(null);
    }
}
