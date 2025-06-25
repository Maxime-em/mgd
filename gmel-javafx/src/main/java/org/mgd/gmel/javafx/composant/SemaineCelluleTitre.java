package org.mgd.gmel.javafx.composant;

import javafx.fxml.Initializable;
import org.mgd.gmel.javafx.connexions.exception.ConnexionsException;
import org.mgd.utilitaire.Strings;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class SemaineCelluleTitre extends SemaineCelluleEntete<LocalDate> implements Initializable {
    private static final DateTimeFormatter formateurSemaine = new DateTimeFormatterBuilder().appendPattern("'Semaine' ww").toFormatter();
    private static final DateTimeFormatter formateurMoisAnnee = new DateTimeFormatterBuilder().appendPattern("MMMM yyyy").toFormatter();

    public SemaineCelluleTitre() throws IOException, ConnexionsException {
        super();

        load();
    }

    public void setDate(LocalDate jour) {
        objet.set(jour);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        getStyleClass().add("semaine-cellule-entete-titre");

        objet.addListener((observable, ancienne, nouvelle) -> libelle.setText(MessageFormat.format(
                "{0}\n{1}",
                nouvelle.format(formateurSemaine),
                Strings.premierCaractereMajuscule(nouvelle.format(formateurMoisAnnee))
        )));
    }
}
