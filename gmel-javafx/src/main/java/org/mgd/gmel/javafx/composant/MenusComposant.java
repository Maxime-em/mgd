package org.mgd.gmel.javafx.composant;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.converter.DefaultStringConverter;
import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.gmel.javafx.composant.cellule.DefautCellule;
import org.mgd.gmel.javafx.composant.evenement.CelluleEvent;
import org.mgd.gmel.javafx.service.ImprimerieService;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class MenusComposant extends TableView<Menu> implements Initializable {
    private final ImprimerieService imprimerieService = ImprimerieService.getInstance();

    @FXML
    private TableColumn<Menu, String> colonne;

    public MenusComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/menus.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setItems(imprimerieService.menusProperty());

        colonne.setCellValueFactory(features -> {
            Menu menu = features.getValue();
            return new SimpleStringProperty(MessageFormat.format("{0} semaine {1}", menu.getAnnee(), menu.getSemaine()));
        });
        colonne.setCellFactory(colonneDeCellule -> new DefautCellule<>(new DefaultStringConverter()));

        addEventHandler(CelluleEvent.<Menu>noeudSupprimerEvenementType(), evenement -> getItems().remove(evenement.getElement()));
    }
}
