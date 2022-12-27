package org.mgd.gmel.javafx.controle;

public enum BoutonIconeTaille {
    NORMALE("normale"),
    PETITE("petite");
    private final String style;

    BoutonIconeTaille(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }
}
