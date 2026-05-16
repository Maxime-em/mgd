package org.mgd.lwjgl.affichage.element.forme;

import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.element.Element;

public class Quadrilatere extends Forme {
    public Quadrilatere(Element<?> parent,
                        String nom,
                        float haut,
                        float droite,
                        float bas,
                        float gauche,
                        float z,
                        float[] textures) {
        float[] positions = {
                droite, haut, z,
                gauche, haut, z,
                gauche, bas, z,
                droite, bas, z
        };
        super(parent, nom, positions, textures, new int[]{0, 1, 3, 3, 1, 2}, positions);
    }

    @Override
    public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
        if (evenementSouris.calcul()) {
            float[] direction = evenementSouris.direction();
            if (direction[0] == 0.0 && direction[1] == 0.0) {
                survole = false;
            } else {
                double differencex = -boite.minimumz() * direction[0] - boite.minimumx();
                double differencey = -boite.minimumz() * direction[1] - boite.minimumy();
                survole = 0 <= differencex && differencex <= boite.maximunx() - boite.minimumx()
                        && 0 <= differencey && differencey <= boite.maximuny() - boite.minimumy();
            }
        } else {
            survole = false;
        }
        return survole;
    }
}
