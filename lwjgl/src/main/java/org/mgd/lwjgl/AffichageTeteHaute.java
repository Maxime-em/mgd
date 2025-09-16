package org.mgd.lwjgl;

import org.lwjgl.nanovg.NVGColor;
import org.mgd.lwjgl.element.Primitif;
import org.mgd.lwjgl.element.Producteur;
import org.mgd.lwjgl.exception.LwjglException;

import java.nio.file.Path;
import java.util.Arrays;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.nvgDelete;

public abstract class AffichageTeteHaute extends Primitif implements Producteur {
    public static final NVGCouleur BLANC;

    static {
        NVGColor nvg = NVGColor.create();
        nvg.r(1.0f);
        nvg.g(1.0f);
        nvg.b(1.0f);
        nvg.a(1.0f);
        BLANC = new NVGCouleur("Blanc", nvg);
    }

    protected final long contexte;
    protected final Fenetre parent;

    protected AffichageTeteHaute(Fenetre parent, boolean estMenu) throws LwjglException {
        this.parent = parent;
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

    protected abstract void dessiner();

    public void dessiner(Ecrit ecrit, float x, float y) {
        nvgFontSize(contexte, ecrit.taille);
        nvgFontFace(contexte, ecrit.police.identifiant());
        nvgTextBounds(contexte, 0f, 0f, ecrit.texte, ecrit.dimensions);
        ecrit.position[0] = x;
        ecrit.position[1] = y;
        nvgFillColor(contexte, ecrit.couleur.nvg());
        nvgText(contexte, x, y, ecrit.texte);
    }

    @Override
    public void produire(Vision vision) {
        if (visible) {
            nvgBeginFrame(contexte, parent.largeur(), parent.hauteur(), 1f);
            dessiner();
            nvgEndFrame(contexte);
        }
    }

    public void liberer() {
        nvgDelete(contexte);
    }

    public boolean visible() {
        return visible;
    }

    public record NVGCouleur(String identifiant, NVGColor nvg) {
    }

    public record NVGPolice(String identifiant, Path fichier, int nvg) {
    }

    public record NVGImage(String identifiant, Path fichier, int largeur, int hauteur, int nvg) {
    }

    public static class Ecrit {
        public final float[] marges;
        public final float[] dimensions;
        public final float[] position;
        public float taille;
        public NVGPolice police;
        public NVGCouleur couleur;
        public String texte;

        public Ecrit(float taille, NVGPolice police, NVGCouleur couleur, String texte, float[] marges) {
            this.marges = Arrays.copyOfRange(marges, 0, 4);
            this.dimensions = new float[4];
            this.position = new float[2];
            this.taille = taille;
            this.police = police;
            this.couleur = couleur;
            this.texte = texte;
        }
    }

    public static class Bouton extends Ecrit {
        public final String identifiant;

        public Bouton(String identifiant, float taille, NVGPolice police, NVGCouleur couleur, String texte, float[] marges) {
            super(taille, police, couleur, texte, marges);
            this.identifiant = identifiant;
        }
    }

    public record Action(Integer index, int abscisse, int ordonnee, int largeur, int hauteur, NVGImage image) {
    }
}
