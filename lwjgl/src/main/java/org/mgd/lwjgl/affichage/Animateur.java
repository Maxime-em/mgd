package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;

public interface Animateur extends Acteur {
    boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris);

    void desurvoler();

    void activer();

    void desactiver();

    default void maj(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (evenementSouris.inacheve()) {
            if (survoler(vision, evenementSouris)) {
                if (evenementSouris.selection()) {
                    activer();
                }
                evenementSouris.comsommer();
            } else if (evenementSouris.selection()) {
                desactiver();
            }
        } else {
            desurvoler();
            if (evenementSouris.selection()) {
                desactiver();
            }
        }
    }
}
