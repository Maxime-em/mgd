package org.mgd.gmel.javafx.composant;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.TransferMode;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.gmel.javafx.composant.cellule.DefautCellule;
import org.mgd.gmel.javafx.composant.evenement.CelluleEvent;
import org.mgd.gmel.javafx.service.BibliothequeService;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class RecettesComposant extends TableView<Recette> implements Initializable {
    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();

    @FXML
    private TableColumn<Recette, String> colonne;

    public RecettesComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/recettes.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setItems(bibliothequeService.recettesProperty());
        setRowFactory(param -> new Ligne());

        DefautCellule.colonneNomParDefaut(colonne, recette -> new SimpleStringProperty(recette.getNom()), bibliothequeService.recetteNomProperty());

        bibliothequeService.recetteProperty().bind(getSelectionModel().selectedItemProperty());

        addEventHandler(CelluleEvent.<Recette>noeudRelacherEvenementType(), evenement -> getItems().remove(evenement.getElement()));
    }

    private static class Ligne extends TableRow<Recette> {
        private Ligne() {
            setOnDragDetected(event -> {
                startDragAndDrop(TransferMode.MOVE).setContent(Map.of(BibliothequeService.FORMAT_DONNEE_RECETTE_UUID, getItem().getIdentifiant()));
                event.consume();
            });
        }
    }
}
