package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Homogeneite;
import org.mgd.lwjgl.Projection;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.element.forme.Forme;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Alignement;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Dimensionnement;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Justification;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Orientation;
import org.mgd.lwjgl.affichage.tetehaute.composant.Action;
import org.mgd.lwjgl.affichage.tetehaute.composant.ActionTextutelle;
import org.mgd.lwjgl.affichage.tetehaute.composant.Liste;
import org.mgd.lwjgl.commun.Animateur;
import org.mgd.lwjgl.commun.Identifiable;
import org.mgd.lwjgl.commun.Survolable;
import org.mgd.lwjgl.exception.LwjglException;

import java.util.*;
import java.util.stream.Stream;

import static org.lwjgl.nanovg.NanoVG.*;

public class ListeActions<T> extends AffichageTeteHaute implements Animateur {
    private final Liste<Action<T>> liste;
    private final List<Identifiable> identifiables;
    private boolean survole;
    private boolean liaisonsSurvoles;

    @SafeVarargs
    public ListeActions(Fenetre parent, int espacement, int marge, ActionTextutelle<T>... actionTextutelles) throws LwjglException {
        super(parent, false, false);
        this.liste = new Liste<>(parent.contexteNvg(), new Disposition(Orientation.VERTICAL, Justification.DEBUT, Alignement.DEBUT, Dimensionnement.VARIABLE, espacement, marge, 0));
        this.liste.actions().addAll(Arrays.asList(actionTextutelles));
        this.identifiables = new LinkedList<>();
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
        identifiables.clear();
        survole = false;
        liaisonsSurvoles = false;
        if (visible) {
            identifiables.addAll(liste.fluxActionsAffichables().filter(action -> action.survoler(vision, evenementSouris)).map(Identifiable.class::cast).toList());
            survole = evenementSouris.inclus(liste.abscisse(), liste.ordonnee(), liste.largeur(), liste.hauteur());
            liaisonsSurvoles = liste.liaisons().stream().anyMatch(liaison -> liaison.visible() && liaison.survoler(vision, evenementSouris));
        }
        return survole;
    }

    @Override
    public void retirer(Vision vision, EvenementSouris evenementSouris) {
        identifiables.clear();
        visible = survole || liaisonsSurvoles;
    }

    @Override
    public Collection<Identifiable> amorcer(boolean droite) {
        visible = identifiables.isEmpty();
        return Stream.concat(identifiables.stream(), Stream.of(liste)).toList();
    }

    @Override
    protected void dessiner() {
        if ((survole || liaisonsSurvoles) && visible) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

            liste.colorier(contexte, EMERAUDE);
            liste.dessiner(contexte);
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
