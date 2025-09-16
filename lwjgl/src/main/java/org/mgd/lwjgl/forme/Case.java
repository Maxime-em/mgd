package org.mgd.lwjgl.forme;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.element.Element;

public class Case extends Forme {
    public Case(Element parent, float haut, float droite, float bas, float gauche, float z, float[] textures, boolean fixe, Colorisation colorisation) {
        super(parent, new float[]{
                droite, haut, z,
                gauche, haut, z,
                gauche, bas, z,
                droite, bas, z
        }, textures, new int[]{0, 1, 3, 3, 1, 2}, fixe, colorisation);
    }

    public Case(Element parent, float haut, float droite, float bas, float gauche, int profondeur, float[] textures, Colorisation colorisation) {
        this(parent, haut, droite, bas, gauche, -1f / (1 + profondeur), textures, true, colorisation);
    }

    @Override
    public void survoler(Fenetre.Souris souris, Matrice<Float> transformation) {
        if (souris.calcul()) {
            if (fixe) {
                float[] coordonnees = souris.coordonnees();
                survole = boite.minimumx() <= coordonnees[0] && coordonnees[0] <= boite.maximunx()
                        && boite.minimumy() <= coordonnees[1] && coordonnees[1] <= boite.maximuny();
            } else {
                float[] direction = souris.direction();
                if (direction[0] == 0.0 && direction[1] == 0.0) {
                    survole = false;
                } else {
                    float[] origine = souris.origine();
                    Boite boiteTransforme = boite.transformer(transformation);
                    double differencex = -(boiteTransforme.minimumz() + origine[2]) * direction[0] - origine[0] - boiteTransforme.minimumx();
                    double differencey = -(boiteTransforme.minimumz() + origine[2]) * direction[1] - origine[1] - boiteTransforme.minimumy();
                    survole = 0 <= differencex && differencex <= boiteTransforme.maximunx() - boiteTransforme.minimumx()
                            && 0 <= differencey && differencey <= boiteTransforme.maximuny() - boiteTransforme.minimumy();
                }
            }
        } else {
            survole = false;
        }
    }
}
