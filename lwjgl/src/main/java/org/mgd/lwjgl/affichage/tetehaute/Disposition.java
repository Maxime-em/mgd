package org.mgd.lwjgl.affichage.tetehaute;

public final class Disposition {
    private final Orientation orientation;
    private final Justification justification;
    private final Alignement alignement;
    private final Dimensionnement dimensionnement;
    private final int espacement;
    private final int marge;
    private final int decalage;
    private int longueur;

    public Disposition(Orientation orientation,
                       Justification justification,
                       Alignement alignement,
                       Dimensionnement dimensionnement,
                       int espacement,
                       int marge,
                       int decalage,
                       int longueur) {
        this.orientation = orientation;
        this.justification = justification;
        this.alignement = alignement;
        this.dimensionnement = dimensionnement;
        this.espacement = espacement;
        this.marge = marge;
        this.decalage = decalage;
        this.longueur = longueur;
    }

    public Orientation orientation() {
        return orientation;
    }

    public Justification justification() {
        return justification;
    }

    public Alignement alignement() {
        return alignement;
    }

    public Dimensionnement dimensionnement() {
        return dimensionnement;
    }

    public int espacement() {
        return espacement;
    }

    public int marge() {
        return marge;
    }

    public int decalage() {
        return decalage;
    }

    public int longueur() {
        return longueur;
    }

    public void longueur(int dimension) {
        longueur = dimension;
    }

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