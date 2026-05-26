package org.mgd.lwjgl.commun;

import org.mgd.lwjgl.Vision;

public interface Acteur extends Sujet {
    void jouer(long ellipse, Vision vision);

    default void produire(long ellipse, Vision vision) {
        if (visible()) {
            jouer(ellipse, vision);
        }
    }
}
