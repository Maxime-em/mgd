package org.mgd.gmel.javafx.composant;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.converter.DefaultStringConverter;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.gmel.javafx.composant.cellule.DefautCellule;
import org.mgd.gmel.javafx.service.ImprimerieService;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class InventaireComposant extends TableView<ProduitQuantifier> implements Initializable {
    private final ImprimerieService imprimerieService = ImprimerieService.getInstance();

    @FXML
    private TableColumn<ProduitQuantifier, String> colonne;

    public InventaireComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/inventaire.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setItems(imprimerieService.produitsQuantifierProperty());

        colonne.setCellValueFactory(features -> {
            ProduitQuantifier produitQuantifier = features.getValue();
            return new SimpleStringProperty(MessageFormat.format("{0}, {1} {2}",
                    produitQuantifier.getProduit().getNom(),
                    produitQuantifier.getQuantite().getValeur(),
                    produitQuantifier.getQuantite().getUnite()));
        });
        colonne.setCellFactory(colonneDeCellule -> new DefautCellule<>(new DefaultStringConverter()));
    }
}
