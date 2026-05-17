package org.mgd.lwjgl;

import org.mgd.commun.Matrice;

public class Homogeneite {
    final Matrice<Float> matrice;
    final Matrice<Float> inverse;

    public Homogeneite(int hauteur, int largeur) {
        matrice = Matrice.parValeurs(4,
                4,
                largeur / 2f, 0f, 0f, largeur / 2f,
                0f, -hauteur / 2f, 0f, hauteur / 2f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f);
        inverse = Matrice.parValeurs(4,
                4,
                2f / largeur, 0f, 0f, -1f,
                0f, -2f / hauteur, 0f, 1f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f);
    }

    public void liberer() {
        // Rien à faire
    }
}
