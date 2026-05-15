package org.mgd.lwjgl;

import org.mgd.lwjgl.Fenetre.EvenementSouris;

public interface Survolable {
    boolean visible();

    boolean survoler(Vision vision, EvenementSouris evenementSouris);
}
