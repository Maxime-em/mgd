package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.lwjgl.nanovg.NVGPaint;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGImage;

import static org.lwjgl.nanovg.NanoVG.*;

public class ActionImagee<T> extends Action<T> {
    private final NVGImage image;

    public ActionImagee(T objet, int largeur, int hauteur, boolean anime, NVGImage image) {
        super(objet, anime);
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.image = image;
    }

    public ActionImagee(int largeur, int hauteur, boolean anime, NVGImage image) {
        this(null, largeur, hauteur, anime, image);
    }

    @Override
    public void dessiner(long contexte) {
        nvgBeginPath(contexte);
        nvgRect(contexte, abscisse(), ordonnee(), largeur(), hauteur());
        nvgFillPaint(contexte, nvgImagePattern(contexte, abscisse, ordonnee, largeur, hauteur, 0, image.nvg(), 1, NVGPaint.create()));
        nvgFill(contexte);
        nvgClosePath(contexte);
    }
}
