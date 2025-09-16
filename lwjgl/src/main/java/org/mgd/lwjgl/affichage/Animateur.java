package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;

public interface Animateur extends Acteur {
    boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris);

    void desurvoler();

    void amorcer(boolean droite);

    void desamorcer(boolean droite);

    default void maj(Vision vision, Fenetre.EvenementSouris evenementSouris, Fenetre.EvenementAmorcages evenementAmorcagesCourant) {
        if (evenementSouris.inacheve()) {
            if (survoler(vision, evenementSouris)) {
                if (evenementSouris.selection()) {
                    amorcer(evenementSouris.droite());
                }
                evenementSouris.comsommer();
            } else if (evenementSouris.selection()) {
                desamorcer(evenementSouris.droite());
            }
        } else {
            desurvoler();
            if (evenementSouris.selection()) {
                desamorcer(evenementSouris.droite());
            }
        }
    }
}
