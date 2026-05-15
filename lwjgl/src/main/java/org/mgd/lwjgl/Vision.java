package org.mgd.lwjgl;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.interne.Ombreur;

public class Vision {
    private final Matrice<Float> matrice = Matrice.identitef(4, 4);

    public void produire() {
        Ombreur.configurer("vision", matrice);
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