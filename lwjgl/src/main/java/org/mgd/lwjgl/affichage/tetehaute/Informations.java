package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.tetehaute.composant.ActionTextutelle;
import org.mgd.lwjgl.exception.LwjglException;

import static org.lwjgl.nanovg.NanoVG.*;

public class Informations extends AffichageTeteHaute {
    private final int abcsisse;
    private final int ordonnee;
    private final int largeur;
    private final int hauteur;
    private ActionTextutelle<Void> actionTextutelle;

    public Informations(Fenetre parent, int abcsisse, int ordonnee, int largeur, int hauteur) throws LwjglException {
        super(parent, false, true);
        this.abcsisse = abcsisse;
        this.ordonnee = ordonnee;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public void ajouter(ActionTextutelle<Void> actionTextutelle) {
        this.actionTextutelle = actionTextutelle;
    }

    public void afficher() {
        if (actionTextutelle != null) {
            int largeurAction = actionTextutelle.dimensionner(contexte).largeur();
            int hauteurAction = actionTextutelle.hauteur();
            actionTextutelle.placer(abcsisse + Math.max(largeur - largeurAction, 0) / 2, ordonnee + Math.max(hauteur - hauteurAction, 0) / 2);
        }
    }

    @Override
    protected void dessiner(long ellipse) {
        if (actionTextutelle != null) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

            nvgBeginPath(contexte);
            nvgRect(contexte, abcsisse, ordonnee, largeur, hauteur);
            nvgFillColor(contexte, EMERAUDE.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            nvgFontSize(contexte, actionTextutelle.taille());
            nvgFontFace(contexte, actionTextutelle.police().identifiant());
            nvgFillColor(contexte, actionTextutelle.couleur().nvg());
            nvgText(contexte, actionTextutelle.abscisse(), actionTextutelle.ordonnee(), actionTextutelle.texte().get());
        }
    }

    @Override
    public void maj(Vision vision, EvenementSouris evenementSouris, EvenementAmorcages evenementAmorcagesCourant) {
        // Rien à faire
    }
}
