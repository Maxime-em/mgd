package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Survolable;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGImage;
import org.mgd.lwjgl.souscription.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActionImagee<T> implements Identifiable, Survolable {
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
    private boolean visible;

    public ActionImagee(T objet, int largeur, int hauteur, boolean anime, NVGImage image) {
        this.image = image;
        this.uuid = UUID.randomUUID();
        this.objet = objet;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.anime = anime;
        this.liaisons = new ArrayList<>();
    }

    public ActionImagee(int largeur, int hauteur, boolean anime, NVGImage image) {
        this(null, largeur, hauteur, anime, image);
    }

    @Override
    public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
        return evenementSouris.inclus(abscisse, ordonnee, largeur, hauteur);
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

    public void afficher() {
        this.visible = true;
    }

    public void masquer() {
        this.visible = false;
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

    @Override
    public boolean visible() {
        return visible;
    }
}
