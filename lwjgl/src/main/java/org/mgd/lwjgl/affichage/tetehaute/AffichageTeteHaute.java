package org.mgd.lwjgl.affichage.tetehaute;

import org.lwjgl.nanovg.NVGColor;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Acteur;
import org.mgd.lwjgl.affichage.Primitif;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGCouleur;
import org.mgd.lwjgl.exception.LwjglException;

import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;

public abstract class AffichageTeteHaute extends Primitif implements Acteur {
    public static final NVGCouleur BLANC;
    public static final NVGCouleur EMERAUDE;
    public static final NVGCouleur ROUGE_COQUELICOT_A50;
    public static final NVGCouleur INDIGO_A50;
    public static final NVGCouleur AUBURN;
    public static final NVGCouleur NOIR_A50;

    static {
        NVGColor nvg = NVGColor.create();
        nvg.r(1f);
        nvg.g(1f);
        nvg.b(1f);
        nvg.a(1f);
        BLANC = new NVGCouleur("Blanc", nvg);

        nvg = NVGColor.create();
        nvg.r(0f);
        nvg.g(102f / 255);
        nvg.b(0f);
        nvg.a(1f);
        EMERAUDE = new NVGCouleur("Émeraude", nvg);

        nvg = NVGColor.create();
        nvg.r(198f / 255);
        nvg.g(8f / 255);
        nvg.b(0f);
        nvg.a(0.5f);
        ROUGE_COQUELICOT_A50 = new NVGCouleur("Rouge coquelicot 50% transparent", nvg);

        nvg = NVGColor.create();
        nvg.r(121f / 255);
        nvg.g(28f / 255);
        nvg.b(248f / 255);
        nvg.a(0.5f);
        INDIGO_A50 = new NVGCouleur("Indigo 50% transparent", nvg);

        nvg = NVGColor.create();
        nvg.r(157f / 255);
        nvg.g(62f / 255);
        nvg.b(12f / 255);
        nvg.a(1f);
        AUBURN = new NVGCouleur("Auburn", nvg);

        nvg = NVGColor.create();
        nvg.r(0f);
        nvg.g(0f);
        nvg.b(0f);
        nvg.a(0.5f);
        NOIR_A50 = new NVGCouleur("Noir 50% transparent", nvg);
    }

    protected final long contexte;

    protected AffichageTeteHaute(Fenetre parent, boolean estMenu, boolean apparaitreParDefaut) throws LwjglException {
        super(parent, apparaitreParDefaut);
        this.contexte = parent.contexteNvg();
        if (this.contexte == 0L) {
            throw new LwjglException("Il faut créer un contexte NVG avant d'instancier un affichage.");
        }

        if (estMenu) {
            parent.ajouterMenu(this);
        } else {
            parent.affichages().add(this);
        }
    }

    protected abstract void dessiner(long ellipse);

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public void jouer(long ellipse, Vision vision) {
        nvgBeginFrame(contexte, parent.largeur(), parent.hauteur(), 1f);
        dessiner(ellipse);
        nvgEndFrame(contexte);
    }

    public void liberer() {
        // Rien à faire
    }
}
