package org.mgd.lwjgl.affichage.element.forme;

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
                new float[]{
                        gauche, bas, z,
                        droite, bas, z,
                        gauche, haut, z
                },
                textures,
                new int[]{0, 1, 3, 3, 1, 2});
    }
}
