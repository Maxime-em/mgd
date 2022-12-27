package org.mgd.gmel.javafx.composant;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import org.mgd.gmel.coeur.objet.Formule;
import org.mgd.gmel.javafx.controle.BoutonFormule;
import org.mgd.gmel.javafx.service.MenuService;
import org.mgd.temps.LocalRepas;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class SemaineCellule extends VBox implements Initializable {
    private final MenuService menuService = MenuService.getInstance();
    private final SimpleObjectProperty<LocalRepas> repas = new SimpleObjectProperty<>();

    public SemaineCellule() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/semaine-cellule.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getStyleClass().add("semaine-cellule");

        repas.addListener((observable, ancien, nouveau) -> {
            getChildren().clear();
            getChildren().addAll(getBoutonFormules(menuService.formulesProperty()));
        });

        ChangeListener<Number> numberChangeListener = (observable, oldValue, newValue) -> {
            getChildren().clear();
            getChildren().addAll(getBoutonFormules(menuService.formulesProperty().get()));
        };
        menuService.formulesProperty().addListener((ListChangeListener<Formule>) change -> {
            menuService.formuleTailleProperty().removeListener(numberChangeListener);
            getChildren().clear();
            getChildren().addAll(getBoutonFormules(change.getList()));
            menuService.formuleTailleProperty().addListener(numberChangeListener);
        });
    }

    private List<BoutonFormule> getBoutonFormules(Collection<? extends Formule> formules) {
        return formules.stream().filter(element -> repas.get() != null && element.enVigueur(repas.get())).map(BoutonFormule::new).toList();
    }

    public SimpleObjectProperty<LocalRepas> repasProperty() {
        return repas;
    }
}
