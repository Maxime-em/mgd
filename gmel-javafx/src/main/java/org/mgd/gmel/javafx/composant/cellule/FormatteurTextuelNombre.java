package org.mgd.gmel.javafx.composant.cellule;

import javafx.scene.control.TextFormatter;

public class FormatteurTextuelNombre<V> extends TextFormatter<V> {
    public FormatteurTextuelNombre() {
        super(change -> {
            if (change.isReplaced() && !change.getText().matches("\\d*")) {
                change.setText(change.getControlText().substring(change.getRangeStart(), change.getRangeEnd()));
            } else if (change.isAdded() && !change.getText().matches("\\d*")) {
                change.setText("");
            }
            return change;
        });
    }
}
