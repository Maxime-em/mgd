package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.exception.LwjglException;

public interface Sujet {
    boolean visible();

    void maj(Vision vision, Fenetre.EvenementSouris evenementSouris, Fenetre.EvenementAmorcages evenementAmorcagesCourant) throws LwjglException;

    void produire(long ellipse, Vision vision);
}
