package org.mgd.lwjgl;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.interne.Ombreur;

public class Vision {
    private final Matrice<Float> transformation = Matrice.identitef(4, 4);

    public void produire() {
        Ombreur.specifier("vision", transformation);
    }

    public void liberer() {
        // Rien à faire
    }

    public void translater(float decalagex, float decalagey, float decalagez) {
        transformation.modifierValeur(0, 3, -decalagex, Float::sum);
        transformation.modifierValeur(1, 3, -decalagey, Float::sum);
        transformation.modifierValeur(2, 3, -decalagez, Float::sum);
    }

    public float translationx() {
        return transformation.valeur(0, 3);
    }

    public float translationy() {
        return transformation.valeur(1, 3);
    }

    public float translationz() {
        return transformation.valeur(2, 3);
    }
}