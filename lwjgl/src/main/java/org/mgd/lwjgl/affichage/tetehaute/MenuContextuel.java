package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Projection;
import org.mgd.lwjgl.TransformationEcran;
import org.mgd.lwjgl.Vue;
import org.mgd.lwjgl.affichage.element.forme.Maillage;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Alignement;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Dimensionnement;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Justification;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Orientation;
import org.mgd.lwjgl.affichage.tetehaute.composant.Entite;
import org.mgd.lwjgl.affichage.tetehaute.composant.Liste;
import org.mgd.lwjgl.commun.Animateur;
import org.mgd.lwjgl.commun.Identifiable;
import org.mgd.lwjgl.commun.Survolable;
import org.mgd.lwjgl.exception.LwjglException;

import java.util.*;
import java.util.stream.Stream;

import static org.lwjgl.nanovg.NanoVG.*;

public class MenuContextuel extends AffichageTeteHaute implements Animateur {
    private final Liste liste;
    private final List<Identifiable> entitesSurvolees;
    private boolean liaisonsSurvoles;
    private boolean survole;

    public MenuContextuel(Fenetre parent, int espacement, int marge, Entite... entites) throws LwjglException {
        super(parent, false, 2);
        this.liste = new Liste(new Disposition(Orientation.VERTICAL, Justification.DEBUT, Alignement.DEBUT, Dimensionnement.VARIABLE, espacement, marge, 0, 0),
                new Options());
        this.liste.ajouter(entites);
        this.entitesSurvolees = new LinkedList<>();
    }

    public void lier(Survolable liaison) {
        liste.lier(liaison);
    }

    @Override
    public Fenetre parent() {
        return parent;
    }

    @Override
    public boolean survoler(Vue vue, EvenementSouris evenementSouris) {
        entitesSurvolees.clear();
        liaisonsSurvoles = false;
        survole = false;
        if (visible) {
            entitesSurvolees.addAll(liste.fluxEntitesAffichables().filter(entite -> entite.survoler(vue, evenementSouris)).map(Identifiable.class::cast).toList());
            liaisonsSurvoles = liste.liaisons().stream().anyMatch(liaison -> liaison.visible() && liaison.survoler(vue, evenementSouris));
            survole = evenementSouris.inclus((float) liste.abscisse(), (float) liste.ordonnee(), (float) liste.largeur(), (float) liste.hauteur());
        }
        return survole;
    }

    @Override
    public void retirer(Vue vue, EvenementSouris evenementSouris) {
        entitesSurvolees.clear();
        visible = survole || liaisonsSurvoles;
    }

    @Override
    public Collection<Identifiable> amorcer(boolean droite) {
        visible = entitesSurvolees.isEmpty();
        return Stream.concat(entitesSurvolees.stream(), Stream.of(liste)).toList();
    }

    @Override
    protected void dessiner() {
        if ((survole || liaisonsSurvoles) && visible) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

            liste.colorier(contexte, EMERAUDE);
            liste.dessiner(contexte);
        }
    }

    public MenuContextuel placer(Projection projection, TransformationEcran transformationEcran, Maillage maillage) {
        Matrice<Float> reduction = maillage.projeterPlanEcran().reduireParLigne(ligne -> Collections.max(Arrays.asList(ligne)));
        Float[] homogene = transformationEcran.matrice().multiplication(projection.matrice()).multiplication(reduction).colonne(0);
        liste.dimensionner(contexte);
        liste.placer(homogene[0].intValue() * homogene[3].intValue(), homogene[1].intValue() * homogene[3].intValue());
        return this;
    }
}
