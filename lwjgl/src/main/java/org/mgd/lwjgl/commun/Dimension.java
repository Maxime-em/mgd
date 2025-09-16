package org.mgd.lwjgl.commun;

public final class Dimension {
    private int abscisse;
    private int ordonnee;
    private int largeur;
    private int hauteur;

    public void placer(int abscisse, int ordonnee) {
        this.abscisse = abscisse;
        this.ordonnee = ordonnee;
    }

    public void proportionner(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public int abscisse() {
        return abscisse;
    }

    public int ordonnee() {
        return ordonnee;
    }

    public int largeur() {
        return largeur;
    }

    public int hauteur() {
        return hauteur;
    }
}
