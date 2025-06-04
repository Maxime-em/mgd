package org.mgd.gmel.javafx.composant;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.TransferMode;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.gmel.javafx.composant.cellule.DefautCellule;
import org.mgd.gmel.javafx.composant.evenement.CelluleEvent;
import org.mgd.gmel.javafx.service.BibliothequeService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

@SuppressWarnings("java:S110")
public class LivresCuisineComposant extends TableView<LivreCuisine> implements Initializable {
    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();

    @FXML
    private TableColumn<LivreCuisine, String> colonne;

    public LivresCuisineComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/livres_cuisine.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setItems(bibliothequeService.livresCuisineProperty());
        setRowFactory(param -> new Ligne());

        DefautCellule.colonneNomParDefaut(colonne, livreCuisine -> new SimpleStringProperty(livreCuisine.getNom()), bibliothequeService.livreCuisineNomProperty());
        bibliothequeService.livreCuisineProperty().bind(getSelectionModel().selectedItemProperty());

        addEventHandler(CelluleEvent.<LivreCuisine>noeudSupprimerEvenementType(), evenement -> getItems().remove(evenement.getElement()));
    }

    private class Ligne extends TableRow<LivreCuisine> {
        private final BooleanBinding ligneValide = bibliothequeService.livreCuisineProperty().isNotEqualTo(itemProperty());

        private Ligne() {
            setOnDragOver(event -> {
                if (event.getDragboard().hasContent(BibliothequeService.FORMAT_DONNEE_RECETTE_UUID) && ligneValide.get()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });
            setOnDragDropped(event -> {
                LivreCuisine livreCuisineCible = getItem();
                bibliothequeService.deplacer((UUID) event.getDragboard().getContent(BibliothequeService.FORMAT_DONNEE_RECETTE_UUID), livreCuisineCible);

                getSelectionModel().select(livreCuisineCible);

                event.setDropCompleted(true);
                event.consume();
            });
        }
    }
}
