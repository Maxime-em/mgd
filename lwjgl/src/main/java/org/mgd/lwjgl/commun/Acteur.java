package org.mgd.lwjgl.commun;

import org.mgd.lwjgl.Vue;

public interface Acteur extends Sujet {
    void jouer(long tempsEcouleMs, Vue vue);

    default void produire(long tempsEcouleMs, Vue vue) {
        if (visible()) {
            jouer(tempsEcouleMs, vue);
        }
    }
}
