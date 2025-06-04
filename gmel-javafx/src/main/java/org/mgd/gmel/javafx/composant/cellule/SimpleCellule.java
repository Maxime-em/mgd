package org.mgd.gmel.javafx.composant.cellule;

import javafx.scene.Node;

@SuppressWarnings("java:S110")
public class SimpleCellule<S, T> extends Cellule<S, T> {
    public SimpleCellule(Node... noeuds) {
        super(noeuds);
    }

    @Override
    public void updateItem(T element, boolean empty) {
        super.updateItem(element, empty);
        if (isEmpty()) {
            setGraphic(null);
        } else {
            setGraphic(this.barreOutils);
        }
    }
}
