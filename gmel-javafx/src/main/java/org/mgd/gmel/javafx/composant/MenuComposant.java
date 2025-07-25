package org.mgd.gmel.javafx.composant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import org.mgd.commun.TypeRepas;
import org.mgd.gmel.javafx.service.BibliothequeService;
import org.mgd.gmel.javafx.service.MenuService;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.temps.LocalRepas;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class MenuComposant extends GridPane implements Initializable {
    public static final Integer NOMBRE_CONVIVES_DEFAUT = 1;
    public static final Integer TAILLE_DEFAUT = 1;

    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();
    private final MenuService menuService = MenuService.getInstance();

    @FXML
    private ChoiceBox<DayOfWeek> jourChoiceBox;
    @FXML
    private ChoiceBox<TypeRepas> typeRepasChoiceBox;

    public MenuComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jourChoiceBox.getItems().setAll(DayOfWeek.values());
        typeRepasChoiceBox.getItems().setAll(TypeRepas.values());

        menuService.menuProperty().addListener((observable, ancien, nouveau) -> {
            jourChoiceBox.getSelectionModel().selectFirst();
            typeRepasChoiceBox.getSelectionModel().selectFirst();
        });

        menuService.referenceProperty().set(LocalDate.now());
    }

    @FXML
    protected void onMouseClickedPrecedent() {
        menuService.precedent();
    }

    @FXML
    protected void onMouseClickedAujourdhui() {
        menuService.aujourdhui();
    }

    @FXML
    protected void onMouseClickedSuivant() {
        menuService.suivant();
    }

    @FXML
    protected void onActionAjouterFormule() throws JaoExecutionException, JaoParseException {
        LocalDate jour = menuService.referenceProperty().get().with(jourChoiceBox.getSelectionModel().getSelectedItem());
        TypeRepas typeRepas = typeRepasChoiceBox.getSelectionModel().getSelectedItem();
        menuService.creerNouvelleFormule(LocalRepas.pour(jour, typeRepas), bibliothequeService.obtenirPremierLivreCuisineNonVide().getRecettes().getFirst());
    }
}
