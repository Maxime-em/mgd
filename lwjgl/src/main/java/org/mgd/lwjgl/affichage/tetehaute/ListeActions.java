package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.*;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.affichage.element.forme.Forme;
import org.mgd.lwjgl.affichage.tetehaute.composant.Ecrit;
import org.mgd.lwjgl.affichage.tetehaute.composant.Liste;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Identifiable;

import java.util.*;
import java.util.stream.Stream;

import static org.lwjgl.nanovg.NanoVG.*;

public class ListeActions<T> extends AffichageTeteHaute implements Animateur {
    private final Liste<T> liste;
    private final List<Identifiable> ecritsSurvoles;
    private boolean survole;
    private boolean liaisonsSurvoles;

    @SafeVarargs
    public ListeActions(Fenetre parent, int espacement, int marge, Ecrit<T>... ecrits) throws LwjglException {
        super(parent, false, false);
        this.liste = new Liste<>(espacement, marge, ecrits);
        this.ecritsSurvoles = new LinkedList<>();
    }

    public void lier(Survolable liaison) {
        liste.lier(liaison);
    }

    @Override
    public Fenetre parent() {
        return parent;
    }

    @Override
    public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
        ecritsSurvoles.clear();
        survole = false;
        liaisonsSurvoles = false;
        if (visible) {
            ecritsSurvoles.addAll(liste.ecrits().stream().filter(ecrit -> ecrit.survoler(vision, evenementSouris)).map(Identifiable.class::cast).toList());
            survole = evenementSouris.inclus(liste.abscisse(), liste.ordonnee(), liste.largeur(), liste.hauteur());
            liaisonsSurvoles = liste.liaisons().stream().anyMatch(liaison -> liaison.visible() && liaison.survoler(vision, evenementSouris));
        }
        return survole;
    }

    @Override
    public void retirer(Vision vision, EvenementSouris evenementSouris) {
        ecritsSurvoles.clear();
        visible = survole || liaisonsSurvoles;
    }

    @Override
    public Collection<Identifiable> amorcer(boolean droite) {
        visible = ecritsSurvoles.isEmpty();
        return Stream.concat(ecritsSurvoles.stream(), Stream.of(liste)).toList();
    }

    @Override
    protected void dessiner(long ellipse) {
        if ((survole || liaisonsSurvoles) && visible) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
            nvgBeginPath(contexte);
            nvgRect(contexte, liste.abscisse(), liste.ordonnee(), liste.largeur(), liste.hauteur());
            nvgFillColor(contexte, NOIR_A15.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            liste.ecrits().forEach(ecrit -> {
                nvgBeginPath(contexte);
                nvgRect(contexte, ecrit.abscisse(), ecrit.ordonnee(), liste.largeur(), ecrit.hauteur());
                nvgFillColor(contexte, EMERAUDE.nvg());
                nvgFill(contexte);
                nvgClosePath(contexte);

                nvgFontSize(contexte, ecrit.taille());
                nvgFontFace(contexte, ecrit.police().identifiant());
                nvgFillColor(contexte, ecrit.couleur().nvg());
                nvgText(contexte, ecrit.abscisse(), ecrit.ordonnee(), ecrit.texte().get());
            });
        }
    }

    public ListeActions<T> placer(Projection projection, Homogeneite homogeneite, Forme forme) {
        Matrice<Float> reduction = forme.projeterPlanEcran().reduireParLigne(ligne -> Collections.max(Arrays.asList(ligne)));
        Float[] homogene = homogeneite.matrice().multiplication(projection.matrice()).multiplication(reduction).colonne(0);
        liste.dimensionner(contexte);
        liste.placer(homogene[0].intValue() * homogene[3].intValue(), homogene[1].intValue() * homogene[3].intValue());
        return this;
    }
}
