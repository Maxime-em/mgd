package org.mgd.gmel.javafx.composant;

import javafx.beans.NamedArg;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import org.mgd.commun.EntryPoint;
import org.mgd.utilitaire.Strings;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class SemaineCelluleEnteteJournee extends SemaineCelluleEntete<LocalDate> implements Initializable {
    private static final DateTimeFormatter formateur = new DateTimeFormatterBuilder().appendPattern("EEEE d").toFormatter();
    private final DayOfWeek jour;

    @EntryPoint
    public SemaineCelluleEnteteJournee(@NamedArg("jourDeSemaine") DayOfWeek jour) throws IOException {
        super();

        this.jour = jour;

        load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        getStyleClass().add("semaine-cellule-entete-ligne");

        objet.addListener((observable, ancienne, nouvelle) -> libelle.setText(Strings.premierCaractereMajuscule(nouvelle.format(formateur))));
    }

    public SimpleObjectProperty<LocalDate> dateProperty() {
        return objet;
    }

    public void setDate(LocalDate date) {
        objet.set(date);
    }

    public DayOfWeek getJour() {
        return jour;
    }
}
