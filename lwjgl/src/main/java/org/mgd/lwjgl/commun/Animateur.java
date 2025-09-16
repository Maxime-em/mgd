package org.mgd.lwjgl.commun;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vue;
import org.mgd.lwjgl.exception.LwjglException;

import java.util.Collection;
import java.util.Collections;

public interface Animateur extends Acteur {
    Fenetre parent();

    void retirer(Vue vue, EvenementSouris evenementSouris);

    Collection<Identifiable> amorcer(boolean droite);

    default void maj(long accumulateur, Vue vue, EvenementSouris evenementSouris, EvenementAmorcages evenementAmorcagesCourant) throws LwjglException {
        if (evenementSouris.inacheve()) {
            if (survoler(vue, evenementSouris)) {
                if (evenementSouris.selection()) {
                    parent().amorcer(amorcer(evenementSouris.droite()), evenementSouris.droite());
                }
                evenementSouris.comsommer();
            } else {
                retirer(vue, evenementSouris);
                if (evenementSouris.selection()) {
                    parent().amorcer(Collections.emptyList(), evenementSouris.droite());
                }
            }
        } else {
            retirer(vue, evenementSouris);
            if (evenementSouris.selection()) {
                parent().amorcer(Collections.emptyList(), evenementSouris.droite());
            }
        }
    }
}
