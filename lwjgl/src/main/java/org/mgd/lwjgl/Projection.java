package org.mgd.lwjgl;

import org.mgd.commun.ConstantesMathematiques;
import org.mgd.commun.Matrice;
import org.mgd.lwjgl.interne.Ombreur;

public class Projection {
    private final Matrice<Float> matrice = Matrice.parValeurs(4,
            4,
            ConstantesMathematiques.RACINE_TROIS * 9f / 16f, 0f, 0f, 0f,
            0f, ConstantesMathematiques.RACINE_TROIS, 0f, 0f,
            0f, 0f, -1f, -2f,
            0f, 0f, -1f, 0f);

    public void produire() {
        Ombreur.configurer("projection", matrice);
    }

    public void liberer() {
        // Rien à faire
    }
}
