package org.mgd.lwjgl.affichage.tetehaute;

import org.lwjgl.nanovg.NVGColor;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Acteur;
import org.mgd.lwjgl.affichage.Primitif;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Activable;
import org.mgd.lwjgl.souscription.Activation;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;

import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;

public abstract class AffichageTeteHaute extends Primitif implements Acteur {
    public static final NVGCouleur BLANC;
    public static final NVGCouleur EMERAUDE;
    public static final NVGCouleur ROUGE_COQUELICOT_A50;
    public static final NVGCouleur INDIGO_A50;
    public static final NVGCouleur AUBURN;

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
    }

    protected final long contexte;

    protected AffichageTeteHaute(Fenetre parent, boolean estMenu) throws LwjglException {
        super(parent);
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

    public record NVGCouleur(String identifiant, NVGColor nvg) {
    }

    public record NVGPolice(String identifiant, Path fichier, int nvg) {
    }

    public record NVGImage(String identifiant, Path fichier, int largeur, int hauteur, int nvg) {
    }

    public static class Ecrit {
        public final float[] dimensions;
        public final float[] position;
        public final float taille;
        public final NVGPolice police;
        public final NVGCouleur couleur;
        public final Supplier<String> texte;

        public Ecrit(float taille, NVGPolice police, NVGCouleur couleur, Supplier<String> texte) {
            this.dimensions = new float[4];
            this.position = new float[2];
            this.taille = taille;
            this.police = police;
            this.couleur = couleur;
            this.texte = texte;
        }
    }

    public static class Bouton<T> extends Ecrit implements Activable<T> {
        public final String identifiant;
        public final T objet;
        private final LinkedList<Activation<T>> activations;

        public Bouton(String identifiant, float taille, NVGPolice police, NVGCouleur couleur, Supplier<String> texte, T objet) {
            super(taille, police, couleur, texte);
            this.activations = new LinkedList<>();
            this.identifiant = identifiant;
            this.objet = objet;
        }

        @Override
        public T elementActivation() {
            return objet;
        }

        @Override
        public Collection<Activation<T>> activations() {
            return activations;
        }
    }

    public static final class Action implements Activable<Void> {
        public final NVGImage image;
        private final LinkedList<Activation<Void>> activations;
        private final int largeur;
        private final int hauteur;
        private final boolean anime;
        private final boolean ephemere;
        private NVGCouleur couleur;
        private int abscisse;
        private int ordonnee;

        public Action(int largeur, int hauteur, boolean anime, boolean ephemere, NVGImage image) {
            this.activations = new LinkedList<>();
            this.image = image;
            this.largeur = largeur;
            this.hauteur = hauteur;
            this.anime = anime;
            this.ephemere = ephemere;
        }

        @Override
        public Void elementActivation() {
            return null;
        }

        @Override
        public Collection<Activation<Void>> activations() {
            return activations;
        }

        public void placer(int abscisse, int ordonnee) {
            this.abscisse = abscisse;
            this.ordonnee = ordonnee;
        }

        public void coloriser(NVGCouleur couleur) {
            this.couleur = couleur;
        }

        public int largeur() {
            return largeur;
        }

        public int hauteur() {
            return hauteur;
        }

        public boolean anime() {
            return anime;
        }

        public boolean ephemere() {
            return ephemere;
        }

        public NVGCouleur couleur() {
            return couleur;
        }

        public int abscisse() {
            return abscisse;
        }

        public int ordonnee() {
            return ordonnee;
        }
    }
}
