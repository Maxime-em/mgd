package org.mgd.gmel.javafx.convertisseur;

import javafx.util.StringConverter;
import org.mgd.gmel.coeur.commun.Mesure;

public class MesureConvertisseur extends StringConverter<Mesure> {
    @Override
    public String toString(Mesure mesure) {
        return mesure.getNom();
    }

    @Override
    public Mesure fromString(String nom) {
        return Mesure.depuisNom(nom);
    }
}
