package org.mgd.gmel.javafx.composant;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.util.converter.LongStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.gmel.javafx.composant.cellule.DefautCellule;
import org.mgd.gmel.javafx.composant.cellule.FormatteurTextuelNombre;
import org.mgd.gmel.javafx.composant.cellule.SimpleCellule;
import org.mgd.gmel.javafx.composant.evenement.CelluleEvent;
import org.mgd.gmel.javafx.controle.BoutonIcone;
import org.mgd.gmel.javafx.controle.BoutonIconeTaille;
import org.mgd.gmel.javafx.controle.BoutonIconeType;
import org.mgd.gmel.javafx.convertisseur.ProduitStringConvertisseur;
import org.mgd.gmel.javafx.service.BibliothequeService;
import org.mgd.gmel.javafx.service.EpicerieService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// TODO on voit la nom de la mesure lors de la sélection
// TODO prévoir un bouton recharger
@SuppressWarnings("java:S110")
public class CompositionComposant extends TableView<ProduitQuantifier> implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(CompositionComposant.class);

    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();
    private final EpicerieService epicerieService = EpicerieService.getInstance();

    @FXML
    private TableColumn<ProduitQuantifier, Boolean> actionColonne;
    @FXML
    private TableColumn<ProduitQuantifier, Produit> nomColonne;
    @FXML
    private TableColumn<ProduitQuantifier, Long> quantiteColonne;
    @FXML
    private TableColumn<ProduitQuantifier, Mesure> mesureColonne;

    public CompositionComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/composition.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setItems(bibliothequeService.recetteProduitsQuantifierProperty());

        actionColonne.setCellValueFactory(param -> Bindings.createBooleanBinding(() -> epicerieService.produitsProperty().stream().anyMatch(element -> element.equals(param.getValue().getProduit()))));
        actionColonne.setCellFactory(param -> new SimpleCellule<>(new BoutonIcone(BoutonIconeType.SUPPRIMER, BoutonIconeTaille.PETITE), new BoutonIcone(BoutonIconeType.AVERTIR, BoutonIconeTaille.PETITE)) {
            @Override
            public void updateItem(Boolean existeDansEpicerie, boolean empty) {
                super.updateItem(existeDansEpicerie, empty);
                boutons.stream().filter(bouton -> bouton.getType() == BoutonIconeType.AVERTIR).findFirst().ifPresent(bouton -> bouton.setVisible(!empty && !existeDansEpicerie));
            }
        });
        nomColonne.setCellValueFactory(param -> new SimpleObjectProperty<>(this, "produit", param.getValue().getProduit()));
        nomColonne.setCellFactory(param -> new ChoiceBoxTableCell<>(new ProduitStringConvertisseur(epicerieService.produitsProperty()), epicerieService.produitsProperty()) {
            @Override
            public void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);

                if (produit == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(produit.getNom());
                }
            }

            @Override
            public void commitEdit(Produit produit) {
                super.commitEdit(produit);
                refresh();
            }
        });
        nomColonne.setOnEditCommit(evenement -> evenement.getRowValue().setProduit(evenement.getNewValue()));
        quantiteColonne.setCellValueFactory(param -> new SimpleObjectProperty<>(this, "quantite-valeur", param.getValue().getQuantite().getValeur()));
        quantiteColonne.setCellFactory(colonne -> new DefautCellule<>(new LongStringConverter(), new FormatteurTextuelNombre<>()));
        quantiteColonne.setOnEditCommit(evenement -> evenement.getRowValue().getQuantite().setValeur(evenement.getNewValue()));
        mesureColonne.setCellValueFactory(param -> new SimpleObjectProperty<>(this, "quantite-mesure", param.getValue().getQuantite().getMesure()));
        mesureColonne.setCellFactory(colonne -> new ChoiceBoxTableCell<>(Mesure.values()) {
            @Override
            public void updateItem(Mesure mesure, boolean empty) {
                super.updateItem(mesure, empty);

                if (mesure == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(mesure.getUnite(false));
                }
            }
        });
        mesureColonne.setOnEditCommit(evenement -> evenement.getRowValue().getQuantite().setMesure(evenement.getNewValue()));

        addEventHandler(CelluleEvent.<ProduitQuantifier>noeudSupprimerEvenementType(), evenement -> getItems().remove(evenement.getElement()));
        addEventHandler(CelluleEvent.<ProduitQuantifier>noeudAvertirEvenementType(), evenement -> LOGGER.warn("Avertissement"));

        epicerieService.produitsProperty().addListener((InvalidationListener) observable -> refresh());
    }
}
