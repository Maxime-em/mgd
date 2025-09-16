package org.mgd.lwjgl;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.interne.GestionnaireOmbrages;

public class Vue {
    private final Matrice<Float> matrice = Matrice.parValeurs(4, 4,
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, -1f,
            0f, 0f, 0f, 1f);

    public void produire() {
        GestionnaireOmbrages.configurer("vision", matrice);
    }

    public void liberer() {
        // Rien à faire
    }

    public void translater(float decalagex, float decalagey, float decalagez) {
        matrice.modifierValeur(0, 3, -decalagex, Float::sum);
        matrice.modifierValeur(1, 3, -decalagey, Float::sum);
        matrice.modifierValeur(2, 3, -decalagez, Float::sum);
    }

    public Matrice<Float> matrice() {
        return matrice;
    }
}