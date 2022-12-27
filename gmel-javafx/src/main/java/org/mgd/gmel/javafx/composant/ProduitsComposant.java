package org.mgd.gmel.javafx.composant;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.gmel.javafx.composant.cellule.DefautCellule;
import org.mgd.gmel.javafx.composant.evenement.CelluleEvent;
import org.mgd.gmel.javafx.service.EpicerieService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class ProduitsComposant extends TableView<Produit> implements Initializable {
    private final EpicerieService epicerieService = EpicerieService.getInstance();

    @FXML
    private TableColumn<Produit, String> colonne;

    public ProduitsComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/produits.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setItems(epicerieService.produitsProperty());

        DefautCellule.colonneNomParDefaut(colonne, produit -> new SimpleStringProperty(produit.getNom()), epicerieService.produitNomProperty());
        epicerieService.produitProperty().bind(getSelectionModel().selectedItemProperty());

        addEventHandler(CelluleEvent.<Produit>noeudRelacherEvenementType(), evenement -> getItems().remove(evenement.getElement()));
    }
}
