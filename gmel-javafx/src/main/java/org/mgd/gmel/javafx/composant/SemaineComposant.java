package org.mgd.gmel.javafx.composant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import org.mgd.commun.TypeRepas;
import org.mgd.gmel.javafx.service.MenuService;
import org.mgd.temps.LocalRepas;
import org.mgd.utilitaire.Dates;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

@SuppressWarnings("java:S110")
public class SemaineComposant extends GridPane implements Initializable {
    private static final String STYLE_CSS_AUJOURDHUI = "aujourdhui";

    private final MenuService menuService = MenuService.getInstance();

    @FXML
    private SemaineCelluleTitre titre;
    @FXML
    private SemaineCelluleEnteteJournee lundi;
    @FXML
    private SemaineCelluleEnteteJournee mardi;
    @FXML
    private SemaineCelluleEnteteJournee mercredi;
    @FXML
    private SemaineCelluleEnteteJournee jeudi;
    @FXML
    private SemaineCelluleEnteteJournee vendredi;
    @FXML
    private SemaineCelluleEnteteJournee samedi;
    @FXML
    private SemaineCelluleEnteteJournee dimanche;
    @FXML
    private SemaineCellule lundiDejeuner;
    @FXML
    private SemaineCellule lundiDiner;
    @FXML
    private SemaineCellule mardiDejeuner;
    @FXML
    private SemaineCellule mardiDiner;
    @FXML
    private SemaineCellule mercrediDejeuner;
    @FXML
    private SemaineCellule mercrediDiner;
    @FXML
    private SemaineCellule jeudiDejeuner;
    @FXML
    private SemaineCellule jeudiDiner;
    @FXML
    private SemaineCellule vendrediDejeuner;
    @FXML
    private SemaineCellule vendrediDiner;
    @FXML
    private SemaineCellule samediDejeuner;
    @FXML
    private SemaineCellule samediDiner;
    @FXML
    private SemaineCellule dimancheDejeuner;
    @FXML
    private SemaineCellule dimancheDiner;

    public SemaineComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/semaine.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getStyleClass().add("semaine");

        Set<SemaineCelluleEnteteJournee> enteteColonnes = Set.of(
                lundi,
                mardi,
                mercredi,
                jeudi,
                vendredi,
                samedi,
                dimanche
        );
        enteteColonnes.forEach(entete -> entete.dateProperty().addListener((observable, ancienne, nouvelle) -> setStyleCssAujourdhui(entete, nouvelle)));

        Set<SemaineCellule> cellules = Set.of(
                lundiDejeuner,
                mardiDejeuner,
                mercrediDejeuner,
                jeudiDejeuner,
                vendrediDejeuner,
                samediDejeuner,
                dimancheDejeuner,
                lundiDiner,
                mardiDiner,
                mercrediDiner,
                jeudiDiner,
                vendrediDiner,
                samediDiner,
                dimancheDiner
        );
        cellules.forEach(cellule -> cellule.repasProperty().addListener((observable, ancien, nouveau) -> setStyleCssAujourdhui(cellule, nouveau)));

        Map<TypeRepas, Map<DayOfWeek, SemaineCellule>> tableauCellules = Map.of(
                TypeRepas.DEJEUNER, Map.of(
                        DayOfWeek.MONDAY, lundiDejeuner,
                        DayOfWeek.TUESDAY, mardiDejeuner,
                        DayOfWeek.WEDNESDAY, mercrediDejeuner,
                        DayOfWeek.THURSDAY, jeudiDejeuner,
                        DayOfWeek.FRIDAY, vendrediDejeuner,
                        DayOfWeek.SATURDAY, samediDejeuner,
                        DayOfWeek.SUNDAY, dimancheDejeuner
                ),
                TypeRepas.DINER, Map.of(
                        DayOfWeek.MONDAY, lundiDiner,
                        DayOfWeek.TUESDAY, mardiDiner,
                        DayOfWeek.WEDNESDAY, mercrediDiner,
                        DayOfWeek.THURSDAY, jeudiDiner,
                        DayOfWeek.FRIDAY, vendrediDiner,
                        DayOfWeek.SATURDAY, samediDiner,
                        DayOfWeek.SUNDAY, dimancheDiner
                )
        );
        menuService.referenceProperty().addListener((observable, ancienne, nouvelle) -> {
            titre.setDate(nouvelle);
            enteteColonnes.forEach(entete -> entete.setDate(Dates.decaler(nouvelle, entete.getJour())));
            tableauCellules.forEach((typeRepas, cellulesTypeRepas) -> cellulesTypeRepas.forEach((jourDeSemaine, cellule) -> {
                cellule.repasProperty().set(LocalRepas.pour(Dates.decaler(nouvelle, jourDeSemaine), typeRepas));
                if (jourDeSemaine.getValue() % 2 == 0) {
                    cellule.getStyleClass().add("paire");
                } else {
                    cellule.getStyleClass().add("impaire");
                }
            }));
        });
    }

    private void setStyleCssAujourdhui(Node noeud, LocalDate nouvelle) {
        if (nouvelle.isEqual(LocalDate.now())) {
            noeud.getStyleClass().add(STYLE_CSS_AUJOURDHUI);
        } else {
            noeud.getStyleClass().remove(STYLE_CSS_AUJOURDHUI);
        }
    }

    private void setStyleCssAujourdhui(Node noeud, LocalRepas nouveau) {
        setStyleCssAujourdhui(noeud, nouveau.getJour());
    }
}
