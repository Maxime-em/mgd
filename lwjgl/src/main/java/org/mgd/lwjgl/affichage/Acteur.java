package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;

public interface Acteur {
    boolean visible();

    void jouer(long ellipse, Vision vision);

    void maj(Fenetre parent, Vision vision, EvenementSouris evenementSouris, EvenementAmorcages evenementAmorcagesCourant);

    default void produire(long ellipse, Vision vision) {
        if (visible()) {
            jouer(ellipse, vision);
        }
    }
}
