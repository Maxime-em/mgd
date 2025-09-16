package org.mgd.lwjgl.commun;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vue;
import org.mgd.lwjgl.exception.LwjglException;

public interface Sujet extends Survolable {
    void maj(long accumulateur, Vue vue, Fenetre.EvenementSouris evenementSouris, Fenetre.EvenementAmorcages evenementAmorcagesCourant) throws LwjglException;

    void produire(long tempsEcouleMs, Vue vue);
}
