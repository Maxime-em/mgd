package org.mgd.lwjgl.commun;

import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vue;

public interface Survolable extends Identifiable {
    boolean visible();

    void apparaitre();

    void disparaitre();

    boolean survoler(Vue vue, EvenementSouris evenementSouris);
}
