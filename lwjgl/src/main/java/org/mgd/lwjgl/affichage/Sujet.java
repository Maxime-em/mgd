package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;

public interface Sujet {
    boolean visible();

    void maj(Vision vision, Fenetre.EvenementSouris evenementSouris, Fenetre.EvenementAmorcages evenementAmorcagesCourant);

    void produire(long ellipse, Vision vision);
}
