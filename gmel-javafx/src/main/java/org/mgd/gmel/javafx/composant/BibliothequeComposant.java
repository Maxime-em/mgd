package org.mgd.gmel.javafx.composant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.gmel.javafx.convertisseur.LivreCuisineStringConvertisseur;
import org.mgd.gmel.javafx.service.BibliothequeService;
import org.mgd.gmel.javafx.service.EpicerieService;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class BibliothequeComposant extends GridPane implements Initializable {
    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();
    private final EpicerieService epicerieService = EpicerieService.getInstance();

    @FXML
    private Button ajouterLivreButton;
    @FXML
    private Button ajouterRecetteButton;
    @FXML
    private Button ajouterProduitButton;
    @FXML
    private Button deplacerRecetteButton;
    @FXML
    private ChoiceBox<LivreCuisine> livreCuisineCibleChoiceBox;

    public BibliothequeComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/bibliotheque.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        livreCuisineCibleChoiceBox.setConverter(new LivreCuisineStringConvertisseur(bibliothequeService.livresCuisineProperty()));
        livreCuisineCibleChoiceBox.setItems(bibliothequeService.livresCuisineProperty().filtered(livreCuisine -> !livreCuisine.equals(bibliothequeService.livreCuisineProperty().get())));

        ajouterProduitButton.disableProperty().bind(bibliothequeService.recetteProperty().isNull());
        deplacerRecetteButton.disableProperty().bind(bibliothequeService.livreCuisineCibleProperty().isNull().or(bibliothequeService.recetteProperty().isNull()));

        bibliothequeService.bibliothequeProperty().addListener((observable, ancienne, nouvelle) -> ajouterLivreButton.setDisable(nouvelle == null));
        bibliothequeService.livreCuisineProperty().addListener((observable, ancien, nouveau) -> {
            ajouterRecetteButton.setDisable(nouveau == null);
            if (nouveau != null) {
                LivreCuisine livreCuisineSelection = bibliothequeService.livreCuisineCibleProperty().get();
                boolean conserverSelection = bibliothequeService.livreCuisineCibleProperty().isNotEqualTo(nouveau).get();
                livreCuisineCibleChoiceBox.setItems(bibliothequeService.livresCuisineProperty().filtered(livreCuisine -> !livreCuisine.equals(nouveau)));
                if (conserverSelection) {
                    livreCuisineCibleChoiceBox.getSelectionModel().select(livreCuisineSelection);
                }
            } else {
                livreCuisineCibleChoiceBox.setItems(bibliothequeService.livresCuisineProperty());
            }
        });
        bibliothequeService.livreCuisineCibleProperty().bind(livreCuisineCibleChoiceBox.getSelectionModel().selectedItemProperty());
    }

    @FXML
    protected void onActionAjouterLivreCuisine() throws JaoExecutionException, JaoParseException {
        bibliothequeService.creerNouveauLivreCuisine();
    }

    @FXML
    protected void onActionAjouterRecette() throws JaoExecutionException, JaoParseException {
        bibliothequeService.creerNouvelleRecette();
    }

    @FXML
    protected void onActionAjouterProduit() throws JaoExecutionException, JaoParseException {
        bibliothequeService.recetteProduitsQuantifierProperty().add(epicerieService.creerNouveauProduitQuantifier());
    }

    @FXML
    protected void onActionDeplacerRecette() {
        Recette recette = bibliothequeService.recetteProperty().get();
        bibliothequeService.recettesProperty().remove(recette);
        bibliothequeService.livreCuisineCibleProperty().get().getRecettes().add(recette);
    }
}
