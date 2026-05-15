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
        super(parent,
                nom,
                new float[]{
                        droite, haut, z,
                        gauche, haut, z,
                        gauche, bas, z,
                        droite, bas, z
                },
                textures,
                new int[]{0, 1, 3, 3, 1, 2});
    }

    @Override
    public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
        if (evenementSouris.calcul()) {
            float[] direction = evenementSouris.direction();
            if (direction[0] == 0.0 && direction[1] == 0.0) {
                survole = false;
            } else {
                Boite boiteTransforme = boite.transformer(parent.transformation()).transformer(deplacement);
                double differencex = -(boiteTransforme.minimumz() + vision.translationz()) * direction[0] - vision.translationx() - boiteTransforme.minimumx();
                double differencey = -(boiteTransforme.minimumz() + vision.translationz()) * direction[1] - vision.translationy() - boiteTransforme.minimumy();
                survole = 0 <= differencex && differencex <= boiteTransforme.maximunx() - boiteTransforme.minimumx()
                        && 0 <= differencey && differencey <= boiteTransforme.maximuny() - boiteTransforme.minimumy();
            }
        } else {
            survole = false;
        }
        return survole;
    }
}
