package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.tetehaute.composant.ActionTextutelle;
import org.mgd.lwjgl.affichage.tetehaute.composant.Fond;
import org.mgd.lwjgl.exception.LwjglException;

import static org.lwjgl.nanovg.NanoVG.*;

public class Informations extends AffichageTeteHaute {
    private final int abcsisse;
    private final int ordonnee;
    private final int largeur;
    private final int hauteur;
    private final Fond fond;
    private ActionTextutelle<Void> actionTextutelle;

    public Informations(Fenetre parent, int abcsisse, int ordonnee, int largeur, int hauteur) throws LwjglException {
        super(parent, false, true);
        this.abcsisse = abcsisse;
        this.ordonnee = ordonnee;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.fond = new Fond(largeur, hauteur);
        this.fond.placer(abcsisse, ordonnee);
    }

    public void ajouter(ActionTextutelle<Void> actionTextutelle) {
        this.actionTextutelle = actionTextutelle;
    }

    public void afficher() {
        if (actionTextutelle != null) {
            actionTextutelle.dimensionner(contexte);
            actionTextutelle.afficher();
            int largeurAction = actionTextutelle.largeur();
            int hauteurAction = actionTextutelle.hauteur();
            actionTextutelle.placer(abcsisse + Math.max(largeur - largeurAction, 0) / 2, ordonnee + Math.max(hauteur - hauteurAction, 0) / 2);
        }
    }

    @Override
    protected void dessiner(long ellipse) {
        if (actionTextutelle != null) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
            fond.colorier(contexte, EMERAUDE);
            actionTextutelle.dessiner(contexte);
        }
    }

    @Override
    public void maj(Vision vision, EvenementSouris evenementSouris, EvenementAmorcages evenementAmorcagesCourant) {
        // Rien à faire
    }
}
