package org.mgd.lwjgl.affichage.element;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.forme.Colorisation;
import org.mgd.lwjgl.forme.Quadrilatere;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class Jeton extends Element<Void> {
    public Jeton(Fenetre parent, String identifiant, float[] coordonnees, float[] translation, Map<String, Path> textures) throws LwjglException {
        super(parent, identifiant, translation, new float[]{1f, 1f, 1f}, new float[]{0f, 0f, 0f}, textures);

        this.formes = Collections.singletonList(new Quadrilatere(this, coordonnees[0], coordonnees[1], coordonnees[2], coordonnees[3], 0f,
                new float[]{
                        1f, 0f, 0f, 0f,
                        0f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f,
                        1f, 1f, 0f, 0f
                }, false, Colorisation.TEXTURE));
    }

    @Override
    public Void elementActivation() {
        return null;
    }
}
