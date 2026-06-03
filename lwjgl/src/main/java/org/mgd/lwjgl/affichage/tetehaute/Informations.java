package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.tetehaute.composant.Entite;
import org.mgd.lwjgl.affichage.tetehaute.composant.Fond;
import org.mgd.lwjgl.commun.Animateur;
import org.mgd.lwjgl.commun.Identifiable;
import org.mgd.lwjgl.exception.LwjglException;

import java.util.Collection;
import java.util.Collections;

import static org.lwjgl.nanovg.NanoVG.*;

public class Informations extends AffichageTeteHaute implements Animateur {
    private final int abcsisse;
    private final int ordonnee;
    private final int largeur;
    private final int hauteur;
    private final Fond fond;
    private Entite entite;

    public Informations(Fenetre parent, int abcsisse, int ordonnee, int largeur, int hauteur) throws LwjglException {
        super(parent, false, true);
        this.abcsisse = abcsisse;
        this.ordonnee = ordonnee;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.fond = new Fond(largeur, hauteur);
        this.fond.placer(abcsisse, ordonnee);
    }

    public void ajouter(Entite entite) {
        this.entite = entite;
    }

    public void afficher() {
        if (entite != null) {
            entite.dimensionner(contexte);
            entite.afficher();
            entite.placer(abcsisse + Math.max(largeur - entite.largeur(), 0) / 2, ordonnee + Math.max(hauteur - entite.hauteur(), 0) / 2);
        }
    }

    @Override
    protected void dessiner() {
        if (entite != null) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
            fond.colorier(contexte, EMERAUDE);
            entite.dessiner(contexte);
        }
    }

    @Override
    public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
        return entite != null && evenementSouris.inclus(abcsisse, ordonnee, largeur, hauteur);
    }

    @Override
    public void retirer(Vision vision, EvenementSouris evenementSouris) {
        // Rien à faire
    }

    @Override
    public Collection<Identifiable> amorcer(boolean droite) {
        return Collections.emptyList();
    }

    @Override
    public Fenetre parent() {
        return parent;
    }
}
