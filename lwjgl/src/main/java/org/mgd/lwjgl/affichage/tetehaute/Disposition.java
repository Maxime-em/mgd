package org.mgd.lwjgl.affichage.tetehaute;

public record Disposition(Orientation orientation,
                          Justification justification,
                          Alignement alignement,
                          Dimensionnement dimensionnement,
                          int espacement,
                          int marge,
                          int longueur) {
    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public enum Justification {
        DEBUT, CENTRAL, ETENDU
    }

    public enum Alignement {
        DEBUT, CENTRAL, FIN
    }

    public enum Dimensionnement {
        FIXE, VARIABLE
    }
}