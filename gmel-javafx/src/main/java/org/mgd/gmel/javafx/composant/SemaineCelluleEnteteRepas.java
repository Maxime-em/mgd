package org.mgd.gmel.javafx.composant;

import javafx.beans.NamedArg;
import javafx.fxml.Initializable;
import org.mgd.commun.EntryPoint;
import org.mgd.commun.TypeRepas;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class SemaineCelluleEnteteRepas extends SemaineCelluleEntete<Void> implements Initializable {
    private final TypeRepas repas;

    @EntryPoint
    public SemaineCelluleEnteteRepas(@NamedArg("typeRepas") TypeRepas repas) throws IOException {
        super();

        this.repas = repas;

        load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        getStyleClass().add("semaine-cellule-entete-colonne");

        libelle.setText(repas.getLabel());
    }
}
