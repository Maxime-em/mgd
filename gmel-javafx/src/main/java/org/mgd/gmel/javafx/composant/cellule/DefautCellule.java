package org.mgd.gmel.javafx.composant.cellule;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.mgd.gmel.javafx.controle.BoutonIcone;
import org.mgd.gmel.javafx.controle.BoutonIconeTaille;
import org.mgd.gmel.javafx.controle.BoutonIconeType;
import org.mgd.jab.objet.Jo;

import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("java:S110")
public class DefautCellule<S, T> extends Cellule<S, T> {
    private final TextField champTextuel;
    private final ObjectProperty<StringConverter<T>> convertisseur;

    public DefautCellule(StringConverter<T> convertisseur, TextFormatter<T> formateur, Node... noeuds) {
        super(noeuds);

        Objects.requireNonNull(convertisseur);

        this.convertisseur = new SimpleObjectProperty<>(convertisseur);
        this.champTextuel = new TextField(getItemText());
        if (formateur != null) {
            this.champTextuel.setTextFormatter(formateur);
        }
        this.champTextuel.setOnAction(evenement -> {
            commitEdit(this.convertisseur.get().fromString(this.champTextuel.getText()));
            evenement.consume();
        });
        this.champTextuel.setOnKeyReleased(evenement -> {
            if (evenement.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
                evenement.consume();
            }
        });
    }

    public DefautCellule(StringConverter<T> convertisseur, Node... noeuds) {
        this(convertisseur, null, noeuds);
    }

    public static <O extends Jo> void colonneNomParDefaut(TableColumn<O, String> colonne, Function<O, ObservableValue<String>> obtenirObservableLibelle, StringPropertyBase stringProperty) {
        colonne.setCellValueFactory(features -> obtenirObservableLibelle.apply(features.getValue()));
        colonne.setCellFactory(colonneDeCellule -> new DefautCellule<>(new DefaultStringConverter(), new BoutonIcone(BoutonIconeType.SUPPRIMER, BoutonIconeTaille.PETITE)));
        colonne.setOnEditCommit(evenement -> {
            stringProperty.set(evenement.getNewValue());
            evenement.consume();
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (this.isEditing()) {
            setText(null);
            setGraphic(this.champTextuel);

            this.champTextuel.setText(getItemText());
            this.champTextuel.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItemText());
        setGraphic(this.barreOutils);
    }

    @Override
    public void updateItem(T element, boolean empty) {
        super.updateItem(element, empty);
        if (isEmpty()) {
            setText(null);
            setGraphic(null);
        } else if (isEditing()) {
            setText(null);
            setGraphic(this.champTextuel);
            this.champTextuel.setText(getItemText());
        } else {
            setText(getItemText());
            setGraphic(this.barreOutils);
        }
    }

    private String getItemText() {
        return convertisseur.get().toString(getItem());
    }
}
