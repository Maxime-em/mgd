package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGCouleur;
import org.mgd.lwjgl.commun.Dimension;
import org.mgd.lwjgl.commun.Survolable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.lwjgl.nanovg.NanoVG.*;

public abstract class Entite implements Survolable {
    protected final UUID uuid;
    protected final List<Survolable> liaisons;
    protected final Dimension dimension;
    protected boolean visible;

    protected Entite() {
        this.uuid = UUID.randomUUID();
        this.liaisons = new ArrayList<>();
        this.dimension = new Dimension();
    }

    protected Entite(int largeur, int hauteur) {
        this();
        proportionner(largeur, hauteur);
    }

    public void lier(Survolable liaison) {
        this.liaisons.add(liaison);
    }

    public void placer(int abscisse, int ordonnee) {
        dimension.placer(abscisse, ordonnee);
    }

    public void proportionner(int largeur, int hauteur) {
        dimension.proportionner(largeur, hauteur);
    }

    public void dimensionner(long contexte) {
        // Rien à faire
    }

    public void dessiner(long contexte) {
        // Rien à faire
    }

    public void colorier(long contexte, NVGCouleur couleur) {
        nvgBeginPath(contexte);
        nvgRect(contexte, abscisse(), ordonnee(), largeur(), hauteur());
        nvgFillColor(contexte, couleur.nvg());
        nvgFill(contexte);
        nvgClosePath(contexte);
    }

    public void afficher() {
        this.visible = true;
    }

    public void masquer() {
        this.visible = false;
    }

    @Override
    public boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        return evenementSouris.inclus(abscisse(), ordonnee(), largeur(), hauteur());
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    public List<Survolable> liaisons() {
        return liaisons;
    }

    public int abscisse() {
        return dimension.abscisse();
    }

    public int ordonnee() {
        return dimension.ordonnee();
    }

    public int largeur() {
        return dimension.largeur();
    }

    public int hauteur() {
        return dimension.hauteur();
    }

    @Override
    public boolean visible() {
        return visible;
    }
}
