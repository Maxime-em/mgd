package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;

public interface Animateur extends Producteur {
    boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris);

    void desurvoler();

    void activer();

    void deactiver();

    default void maj(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (evenementSouris.inacheve()) {
            if (survoler(vision, evenementSouris)) {
                if (evenementSouris.selection()) {
                    activer();
                }
                evenementSouris.comsommer();
            } else if (evenementSouris.selection()) {
                deactiver();
            }
        } else {
            desurvoler();
            if (evenementSouris.selection()) {
                deactiver();
            }
        }
    }
}
