package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Identifiable;

import java.util.Collection;
import java.util.Collections;

public interface Animateur extends Acteur {
    Fenetre parent();

    boolean survoler(Vision vision, EvenementSouris evenementSouris);

    void retirer(Vision vision, EvenementSouris evenementSouris);

    Collection<Identifiable> amorcer(boolean droite) throws LwjglException;

    default void maj(Vision vision, EvenementSouris evenementSouris, EvenementAmorcages evenementAmorcagesCourant) throws LwjglException {
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
