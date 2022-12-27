package org.mgd.gmel.javafx.composant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import org.mgd.gmel.javafx.service.EpicerieService;
import org.mgd.jab.persistence.exception.JaoExecutionException;

import java.io.IOException;

@SuppressWarnings("java:S110")
public class EpicerieComposant extends GridPane {
    private final EpicerieService epicerieService = EpicerieService.getInstance();

    public EpicerieComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/epicerie.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @FXML
    protected void onActionAjouterProduit() throws JaoExecutionException {
        epicerieService.creerNouveauProduit();
    }
}
