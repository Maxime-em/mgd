package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;

public interface Acteur {
    boolean visible();

    void jouer(long ellipse, Vision vision);

    void maj(Vision vision, Fenetre.EvenementSouris evenementSouris);

    default void produire(long ellipse, Vision vision) {
        if (visible()) {
            jouer(ellipse, vision);
        }
    }
}
