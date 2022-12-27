package org.mgd.gmel.javafx.controle;

public enum BoutonIconeType {
    SUPPRIMER("forme-supprimer"),
    AJOUTER("forme-ajouter"),
    AVERTIR("forme-avertir");

    private final String style;

    BoutonIconeType(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }
}
