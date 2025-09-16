package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;

public interface Producteur {
    void produire(Vision vision);

    void maj(Vision vision, Fenetre.EvenementSouris evenementSouris);
}
