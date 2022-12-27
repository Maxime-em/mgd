package org.mgd.gmel.javafx.composant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import org.mgd.gmel.javafx.service.BibliothequeService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class RecetteComposant extends VBox implements Initializable {
    private static final int NOMBRE_PERSONNE_MINIMUM = 1;
    private static final int NOMBRE_PERSONNE_MAXIMUM = Integer.MAX_VALUE;

    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();

    @FXML
    private Spinner<Integer> nombrePersonnesSpinner;

    public RecetteComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/recette.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getStyleClass().add("recette");
        disableProperty().bind(bibliothequeService.recetteProperty().isNull());

        nombrePersonnesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(NOMBRE_PERSONNE_MINIMUM, NOMBRE_PERSONNE_MAXIMUM));

        bibliothequeService.recetteProperty().addListener((observable, ancienne, nouvelle) -> nombrePersonnesSpinner.getValueFactory().setValue(nouvelle != null ? nouvelle.getNombrePersonnes() : null));
        bibliothequeService.recetteNombrePersonnesProperty().bind(nombrePersonnesSpinner.valueProperty());
    }
}
