package org.mgd.gmel.javafx.composant.cellule;

import javafx.scene.Node;

@SuppressWarnings("java:S110")
public class VoidCellule<S> extends Cellule<S, Void> {
    public VoidCellule(Node... noeuds) {
        super(noeuds);
    }

    @Override
    public void updateItem(Void element, boolean empty) {
        super.updateItem(element, empty);
        if (isEmpty()) {
            setGraphic(null);
        } else {
            setGraphic(this.barreOutils);
        }
    }
}
