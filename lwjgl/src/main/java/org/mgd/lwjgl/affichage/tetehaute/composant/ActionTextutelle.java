package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGCouleur;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGPolice;

import java.util.function.Supplier;

import static org.lwjgl.nanovg.NanoVG.*;

public class ActionTextutelle<T> extends Action<T> {
    private final float taille;
    private final NVGPolice police;
    private final NVGCouleur couleur;
    private final Supplier<String> texte;

    public ActionTextutelle(T objet, float taille, NVGPolice police, NVGCouleur couleur, Supplier<String> texte) {
        super(objet);
        this.visible = true;
        this.taille = taille;
        this.police = police;
        this.couleur = couleur;
        this.texte = texte;
    }

    public ActionTextutelle(float taille, NVGPolice police, NVGCouleur couleur, Supplier<String> texte) {
        this(null, taille, police, couleur, texte);
    }

    @Override
    public void dimensionner(long contexte) {
        float[] dimensions = new float[4];
        nvgFontSize(contexte, taille);
        nvgFontFace(contexte, police.identifiant());
        nvgTextBounds(contexte, 0f, 0f, texte.get(), dimensions);
        proportionner((int) Math.ceil(dimensions[2] - dimensions[0]), (int) Math.ceil(dimensions[3] - dimensions[1]));
    }

    @Override
    public void dessiner(long contexte) {
        nvgFontSize(contexte, taille);
        nvgFontFace(contexte, police.identifiant());
        nvgFillColor(contexte, couleur.nvg());
        nvgText(contexte, abscisse(), ordonnee(), texte.get());
    }

    public float taille() {
        return taille;
    }

    public NVGPolice police() {
        return police;
    }

    public NVGCouleur couleur() {
        return couleur;
    }

    public Supplier<String> texte() {
        return texte;
    }
}
