package org.mgd.lwjgl;

public interface Survolable {
    boolean visible();
    boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris);
}
