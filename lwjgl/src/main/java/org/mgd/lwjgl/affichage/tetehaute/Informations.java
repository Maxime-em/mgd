package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.tetehaute.composant.Ecrit;
import org.mgd.lwjgl.exception.LwjglException;

import static org.lwjgl.nanovg.NanoVG.*;

public class Informations extends AffichageTeteHaute {
    private final int abcsisse;
    private final int ordonnee;
    private final int largeur;
    private final int hauteur;
    private Ecrit<Void> ecrit;

    public Informations(Fenetre parent, int abcsisse, int ordonnee, int largeur, int hauteur) throws LwjglException {
        super(parent, false);
        this.abcsisse = abcsisse;
        this.ordonnee = ordonnee;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public void ajouter(Ecrit<Void> ecrit) {
        this.ecrit = ecrit;
    }

    public void afficher() {
        if (ecrit != null) {
            int largeurEcrit = ecrit.dimensionner(contexte).largeur();
            int hauteurEcrit = ecrit.hauteur();
            ecrit.abscisse(abcsisse + Math.max(largeur - largeurEcrit, 0) / 2);
            ecrit.ordonnee(ordonnee + Math.max(hauteur - hauteurEcrit, 0) / 2);
        }
    }

    @Override
    protected void dessiner(long ellipse) {
        if (ecrit != null) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

            nvgBeginPath(contexte);
            nvgRect(contexte, abcsisse, ordonnee, largeur, hauteur);
            nvgFillColor(contexte, EMERAUDE.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            nvgFontSize(contexte, ecrit.taille());
            nvgFontFace(contexte, ecrit.police().identifiant());
            nvgFillColor(contexte, ecrit.couleur().nvg());
            nvgText(contexte, ecrit.abscisse(), ecrit.ordonnee(), ecrit.texte().get());
        }
    }

    @Override
    public void maj(Fenetre fenetre, Vision vision, Fenetre.EvenementSouris evenementSouris, EvenementAmorcages evenementAmorcagesCourant) {
        // Rien à faire
    }
}
