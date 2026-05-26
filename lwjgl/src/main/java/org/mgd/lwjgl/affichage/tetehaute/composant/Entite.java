package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGCouleur;
import org.mgd.lwjgl.commun.Survolable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.lwjgl.nanovg.NanoVG.*;

public abstract class Entite implements Survolable {
    protected final UUID uuid;
    protected final List<Survolable> liaisons;
    protected int abscisse;
    protected int ordonnee;
    protected int largeur;
    protected int hauteur;
    protected boolean visible;

    protected Entite() {
        this.uuid = UUID.randomUUID();
        this.liaisons = new ArrayList<>();
    }

    public void lier(Survolable liaison) {
        this.liaisons.add(liaison);
    }

    public void placer(int abscisse, int ordonnee) {
        this.abscisse = abscisse;
        this.ordonnee = ordonnee;
    }

    public void dimensionner(long contexte) {
        // Rien à faire
    }

    public void dessiner(long contexte) {
        // Rien à faire
    }

    public void colorier(long contexte, NVGCouleur couleur) {
        nvgBeginPath(contexte);
        nvgRect(contexte, abscisse, ordonnee, largeur, hauteur);
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
        return evenementSouris.inclus(abscisse, ordonnee, largeur, hauteur);
    }

    @Override
    public UUID uuid() {
        return uuid;
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

    public int largeur() {
        return largeur;
    }

    public int hauteur() {
        return hauteur;
    }

    @Override
    public boolean visible() {
        return visible;
    }
}
