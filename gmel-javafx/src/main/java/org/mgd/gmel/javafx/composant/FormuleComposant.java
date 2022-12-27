package org.mgd.gmel.javafx.composant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.gmel.javafx.convertisseur.LivreCuisineStringConvertisseur;
import org.mgd.gmel.javafx.convertisseur.RecetteStringConvertisseur;
import org.mgd.gmel.javafx.service.BibliothequeService;
import org.mgd.gmel.javafx.service.MenuService;

import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class FormuleComposant extends VBox implements Initializable {
    private static final int NOMBRE_CONVIVES_MINIMUM = 1;
    private static final int NOMBRE_CONVIVES_MAXIMUM = Integer.MAX_VALUE;
    private static final int TAILLE_PERIODE_MINIMUM = 1;

    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();
    private final MenuService menuService = MenuService.getInstance();

    @FXML
    private ChoiceBox<LivreCuisine> livreCuisineChoiceBox;
    @FXML
    private ChoiceBox<Recette> recetteChoiceBox;
    @FXML
    private Spinner<Integer> taillePeriodeSpinner;
    @FXML
    private Spinner<Integer> nombreConvivesSpinner;

    public FormuleComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/formule.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getStyleClass().add("formule");

        livreCuisineChoiceBox.setConverter(new LivreCuisineStringConvertisseur(bibliothequeService.livresCuisineProperty()));
        livreCuisineChoiceBox.setItems(bibliothequeService.livresCuisineProperty());
        recetteChoiceBox.setConverter(new RecetteStringConvertisseur(bibliothequeService.recettesProperty()));
        recetteChoiceBox.setItems(menuService.formuleRecettesProperty());

        menuService.formuleProperty().addListener((observable, ancienne, nouvelle) -> {
            setDisable(nouvelle == null);
            if (nouvelle != null) {
                try {
                    livreCuisineChoiceBox.getSelectionModel().select(bibliothequeService.obtenirPremierLivreCuisineContenant(nouvelle.getRecette()));
                    recetteChoiceBox.getSelectionModel().select(nouvelle.getRecette());
                } catch (NoSuchElementException e) {
                    livreCuisineChoiceBox.getSelectionModel().clearSelection();
                    recetteChoiceBox.getSelectionModel().clearSelection();
                }
                nombreConvivesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(NOMBRE_CONVIVES_MINIMUM, NOMBRE_CONVIVES_MAXIMUM, nouvelle.getNombreConvives()));
                taillePeriodeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(TAILLE_PERIODE_MINIMUM, (int) (menuService.formuleProperty().get().getPeriode().getRepas().nombreDemiesJournees() + 1), nouvelle.getPeriode().getTaille()));
            } else {
                livreCuisineChoiceBox.getSelectionModel().clearSelection();
                recetteChoiceBox.getSelectionModel().clearSelection();
                nombreConvivesSpinner.setValueFactory(null);
                taillePeriodeSpinner.setValueFactory(null);
            }
        });

        menuService.formuleLivreCuisineProperty().bind(livreCuisineChoiceBox.getSelectionModel().selectedItemProperty());
        menuService.formuleRecetteProperty().bind(recetteChoiceBox.getSelectionModel().selectedItemProperty());
        menuService.formuleNombreConvivesProperty().bind(nombreConvivesSpinner.valueProperty());
        menuService.formuleTailleProperty().bind(taillePeriodeSpinner.valueProperty());
    }
}
