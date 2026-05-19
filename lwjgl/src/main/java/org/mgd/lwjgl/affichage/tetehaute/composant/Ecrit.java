package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Survolable;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGCouleur;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGPolice;
import org.mgd.lwjgl.souscription.Identifiable;

import java.util.UUID;
import java.util.function.Supplier;

import static org.lwjgl.nanovg.NanoVG.*;

public class Ecrit<T> implements Identifiable, Survolable {
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
    public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
        return evenementSouris.inclus(abscisse, ordonnee, largeur(), hauteur());
    }

    public void placer(int abscisse, int ordonnee) {
        this.abscisse = abscisse;
        this.ordonnee = ordonnee;
    }

    public void elargir(int largeur) {
        dimensions[2] = largeur - dimensions[0];
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    public T objet() {
        return objet;
    }

    public float[] dimensions() {
        return dimensions;
    }

    public int largeur() {
        return (int) Math.ceil(dimensions[2] - dimensions[0]);
    }

    public int hauteur() {
        return (int) Math.ceil(dimensions[3] - dimensions[1]);
    }

    public int abscisse() {
        return abscisse;
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

    @Override
    public boolean visible() {
        return true;
    }
}
