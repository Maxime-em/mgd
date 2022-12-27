package org.mgd.gmel.javafx.composant.cellule;

import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.mgd.gmel.javafx.composant.evenement.CelluleEvent;

import java.util.Arrays;

@SuppressWarnings("java:S110")
public abstract class Cellule<S, T> extends TableCell<S, T> {
    protected final HBox barreOutils;

    protected Cellule(Node... noeuds) {
        this.barreOutils = noeuds.length > 0 ? new HBox(noeuds) : null;
        Arrays.stream(noeuds).forEach(noeud -> noeud.addEventHandler(MouseEvent.MOUSE_RELEASED, evenement -> fireEvent(new CelluleEvent<>(CelluleEvent.noeudRelacherEvenementType(), this.getTableRow().getItem()))));
    }
}
