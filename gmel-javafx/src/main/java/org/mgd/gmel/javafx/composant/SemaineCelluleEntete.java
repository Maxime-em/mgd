package org.mgd.gmel.javafx.composant;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public abstract class SemaineCelluleEntete<T> extends AnchorPane implements Initializable {
    protected final SimpleObjectProperty<T> objet = new SimpleObjectProperty<>();

    @FXML
    protected Label libelle;

    protected void load() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/semaine-cellule-entete.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getStyleClass().add("semaine-cellule-entete");
    }
}
