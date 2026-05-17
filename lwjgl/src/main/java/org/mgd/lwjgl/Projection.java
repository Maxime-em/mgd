package org.mgd.lwjgl;

import org.mgd.commun.ConstantesMathematiques;
import org.mgd.commun.Matrice;
import org.mgd.lwjgl.interne.Ombreur;

public class Projection {
    final Matrice<Float> matrice;
    final Matrice<Float> inverse;

    public Projection(int ratioNumerateur, int ratioDenominateur) {
        matrice = Matrice.parValeurs(4,
                4,
                ConstantesMathematiques.RACINE_TROIS * ratioDenominateur / ratioNumerateur, 0f, 0f, 0f,
                0f, ConstantesMathematiques.RACINE_TROIS, 0f, 0f,
                0f, 0f, -1f, -2f,
                0f, 0f, -1f, 0f);
        inverse = Matrice.parValeurs(4,
                4,
                ratioNumerateur / (ConstantesMathematiques.RACINE_TROIS * ratioDenominateur), 0f, 0f, 0f,
                0f, 1f / ConstantesMathematiques.RACINE_TROIS, 0f, 0f,
                0f, 0f, 0f, -1f,
                0f, 0f, -0.5f, 0.5f);
    }

    public void produire() {
        Ombreur.configurer("projection", matrice);
    }

    public void liberer() {
        // Rien à faire
    }
}
