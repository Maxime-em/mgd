package org.mgd.lwjgl.affichage.tetehaute;

public record Disposition(Orientation orientation,
                          Justification justification,
                          Alignement alignement,
                          Position position,
                          int espacement) {
    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public enum Justification {
        DEBUT, CENTRAL, ETENDU
    }

    public enum Alignement {
        DEBUT, CENTRAL, FIN
    }

    public enum Position {
        HAUT, DROITE, BAS, GAUCHE
    }
}