package org.mgd.lwjgl.affichage.tetehaute;

import org.lwjgl.nanovg.NVGColor;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Survolable;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Acteur;
import org.mgd.lwjgl.affichage.Primitif;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Identifiable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.lwjgl.nanovg.NanoVG.*;

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

    public static class Ecrit<T> implements Identifiable {
        private final UUID uuid;
        private final T objet;
        private final float[] dimensions;
        private final float taille;
        private final NVGPolice police;
        private final NVGCouleur couleur;
        private final Supplier<String> texte;
        private int abscisse;
        private int ordonnee;

        public Ecrit(T objet, float taille, NVGPolice police, NVGCouleur couleur, Supplier<String> texte) {
            this.uuid = UUID.randomUUID();
            this.objet = objet;
            this.dimensions = new float[4];
            this.taille = taille;
            this.police = police;
            this.couleur = couleur;
            this.texte = texte;
        }

        public Ecrit(float taille, NVGPolice police, NVGCouleur couleur, Supplier<String> texte) {
            this(null, taille, police, couleur, texte);
        }

        public Ecrit<T> dimensionner(long contexte) {
            nvgFontSize(contexte, taille);
            nvgFontFace(contexte, police.identifiant());
            nvgTextBounds(contexte, 0f, 0f, texte.get(), dimensions);
            return this;
        }

        @Override
        public UUID uuid() {
            return uuid;
        }

        public T objet() {
            return objet;
        }

        public int largeur() {
            return (int) Math.ceil(dimensions[2] - dimensions[0]);
        }

        public int hauteur() {
            return (int) Math.ceil(dimensions[3] - dimensions[1]);
        }

        public void abscisse(int abscisse) {
            this.abscisse = abscisse;
        }

        public int abscisse() {
            return abscisse;
        }

        public void ordonnee(int ordonnee) {
            this.ordonnee = ordonnee;
        }

        public int ordonnee() {
            return ordonnee;
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

    public static class Action<T> implements Identifiable, Survolable {
        private final NVGImage image;
        private final UUID uuid;
        private final T objet;
        private final int largeur;
        private final int hauteur;
        private final boolean anime;
        private final List<Survolable> liaisons;
        private int abscisse;
        private int ordonnee;
        private boolean active;

        public Action(T objet, int largeur, int hauteur, boolean anime, NVGImage image) {
            this.image = image;
            this.uuid = UUID.randomUUID();
            this.objet = objet;
            this.largeur = largeur;
            this.hauteur = hauteur;
            this.anime = anime;
            this.liaisons = new ArrayList<>();
        }

        public Action(int largeur, int hauteur, boolean anime, NVGImage image) {
            this(null, largeur, hauteur, anime, image);
        }

        @Override
        public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
            return evenementSouris.inclus(this);
        }

        public void placer(int abscisse, int ordonnee) {
            this.abscisse = abscisse;
            this.ordonnee = ordonnee;
        }

        public void activer() {
            this.active = true;
        }

        public void desactiver() {
            this.active = false;
        }

        public void lier(Survolable liaison) {
            this.liaisons.add(liaison);
        }

        public NVGImage image() {
            return image;
        }

        @Override
        public UUID uuid() {
            return uuid;
        }

        public T objet() {
            return objet;
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

        public List<Survolable> liaisons() {
            return liaisons;
        }

        public int abscisse() {
            return abscisse;
        }

        public int ordonnee() {
            return ordonnee;
        }

        public boolean active() {
            return active;
        }
    }
}
