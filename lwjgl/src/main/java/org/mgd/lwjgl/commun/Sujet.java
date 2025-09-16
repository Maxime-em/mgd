package org.mgd.lwjgl.commun;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.exception.LwjglException;

public interface Sujet extends Survolable {
    void maj(long accumulateur, Vision vision, Fenetre.EvenementSouris evenementSouris, Fenetre.EvenementAmorcages evenementAmorcagesCourant) throws LwjglException;

    void produire(long ellipse, Vision vision);
}
