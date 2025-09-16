package org.mgd.lwjgl.forme;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.element.Element;

public class Quadrilatere extends Forme {
    public Quadrilatere(Element<?> parent, float haut, float droite, float bas, float gauche, float z, float[] textures, boolean fixe, Colorisation colorisation) {
        super(parent, new float[]{
                droite, haut, z,
                gauche, haut, z,
                gauche, bas, z,
                droite, bas, z
        }, textures, new int[]{0, 1, 3, 3, 1, 2}, fixe, colorisation);
    }

    @Override
    public boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris, Matrice<Float> transformation) {
        if (evenementSouris.calcul()) {
            if (fixe) {
                float[] coordonnees = evenementSouris.coordonneesHomogenes();
                survole = boite.minimumx() <= coordonnees[0] && coordonnees[0] <= boite.maximunx()
                        && boite.minimumy() <= coordonnees[1] && coordonnees[1] <= boite.maximuny();
            } else {
                float[] direction = evenementSouris.direction();
                if (direction[0] == 0.0 && direction[1] == 0.0) {
                    survole = false;
                } else {
                    Boite boiteTransforme = boite.transformer(transformation);
                    double differencex = -(boiteTransforme.minimumz() + vision.translationz()) * direction[0] - vision.translationx() - boiteTransforme.minimumx();
                    double differencey = -(boiteTransforme.minimumz() + vision.translationz()) * direction[1] - vision.translationy() - boiteTransforme.minimumy();
                    survole = 0 <= differencex && differencex <= boiteTransforme.maximunx() - boiteTransforme.minimumx()
                            && 0 <= differencey && differencey <= boiteTransforme.maximuny() - boiteTransforme.minimumy();
                }
            }
        } else {
            survole = false;
        }
        return survole;
    }
}
