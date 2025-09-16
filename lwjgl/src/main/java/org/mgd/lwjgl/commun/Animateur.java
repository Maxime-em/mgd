package org.mgd.lwjgl.commun;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.exception.LwjglException;

import java.util.Collection;
import java.util.Collections;

public interface Animateur extends Acteur {
    Fenetre parent();

    void retirer(Vision vision, EvenementSouris evenementSouris);

    Collection<Identifiable> amorcer(boolean droite);

    default void maj(long accumulateur, Vision vision, EvenementSouris evenementSouris, EvenementAmorcages evenementAmorcagesCourant) throws LwjglException {
        if (evenementSouris.inacheve()) {
            if (survoler(vision, evenementSouris)) {
                if (evenementSouris.selection()) {
                    parent().amorcer(amorcer(evenementSouris.droite()), evenementSouris.droite());
                }
                evenementSouris.comsommer();
            } else {
                retirer(vision, evenementSouris);
                if (evenementSouris.selection()) {
                    parent().amorcer(Collections.emptyList(), evenementSouris.droite());
                }
            }
        } else {
            retirer(vision, evenementSouris);
            if (evenementSouris.selection()) {
                parent().amorcer(Collections.emptyList(), evenementSouris.droite());
            }
        }
    }
}
