package org.mgd.gmel.javafx.composant;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import org.mgd.gmel.coeur.commun.TypeDocument;
import org.mgd.gmel.javafx.GmelSingletons;
import org.mgd.gmel.javafx.composant.exception.ComposantException;
import org.mgd.gmel.javafx.convertisseur.TypeDocumentStringConvertisseur;
import org.mgd.gmel.javafx.persistence.exception.ConnectionException;
import org.mgd.gmel.javafx.service.ImprimerieService;
import org.mgd.gmel.pdf.Pabm;
import org.mgd.pam.exception.PabException;
import org.mgd.utilitaire.Dates;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ResourceBundle;

@SuppressWarnings("java:S110")
public class ImprimerieComposant extends GridPane implements Initializable {
    private final ImprimerieService imprimerieService = ImprimerieService.getInstance();
    private final SimpleListProperty<TypeDocument> typesDocuments = new SimpleListProperty<>(this, "types-de-documents", FXCollections.observableArrayList(TypeDocument.values()));

    @FXML
    private ChoiceBox<TypeDocument> typeDocumentChoiceBox;
    @FXML
    private DatePicker debutDatePicker;
    @FXML
    private DatePicker finDatePicker;

    public ImprimerieComposant() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/imprimerie.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeDocumentChoiceBox.setItems(typesDocuments);
        typeDocumentChoiceBox.setConverter(new TypeDocumentStringConvertisseur());
        typeDocumentChoiceBox.getSelectionModel().selectFirst();

        debutDatePicker.setValue(Dates.decaler(LocalDate.now(), DayOfWeek.MONDAY));
        finDatePicker.setValue(Dates.decaler(LocalDate.now(), DayOfWeek.SUNDAY));
    }

    @FXML
    private void majAgenda() {
        if (debutDatePicker.valueProperty().isNotNull().get() && finDatePicker.valueProperty().isNotNull().get()) {
            imprimerieService.remplirAgenda(debutDatePicker.getValue(), finDatePicker.getValue());
            imprimerieService.remplirInventaire();
        }
    }

    @FXML
    private void onActionGenererPdf() {
        try {
            Pabm pabm = GmelSingletons.connexion().getPabm();
            if (typeDocumentChoiceBox.getValue() == TypeDocument.MENUS) {
                Desktop.getDesktop().open(pabm.ecrire(imprimerieService.agendaProperty().get()).toFile());
            } else {
                Desktop.getDesktop().open(pabm.ecrire(imprimerieService.inventaireProperty().get()).toFile());
            }
        } catch (PabException | ConnectionException | IOException e) {
            throw new ComposantException(e);
        }
    }
}
