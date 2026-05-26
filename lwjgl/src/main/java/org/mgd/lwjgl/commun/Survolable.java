package org.mgd.lwjgl.commun;

import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;

public interface Survolable extends Identifiable {
    boolean visible();

    boolean survoler(Vision vision, EvenementSouris evenementSouris);
}
