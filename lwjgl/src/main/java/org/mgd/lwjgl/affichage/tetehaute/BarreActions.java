package org.mgd.lwjgl.affichage.tetehaute;

import org.lwjgl.nanovg.NVGPaint;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Activable;
import org.mgd.lwjgl.souscription.Desactivable;
import org.mgd.lwjgl.souscription.Desactivation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.lwjgl.nanovg.NanoVG.*;

public class BarreActions extends AffichageTeteHaute implements Animateur, Desactivable {
    private final LinkedList<Desactivation> desactivations;
    private final Orientation orientation;
    private final Justification justification;
    private final Ajustement ajustement;
    private final int espacement;
    private final int abcisses;
    private final int ordonnee;
    private final int longueur;
    private final int epaisseur;
    private final List<Action> actions;
    private List<Action> actionsSurvolees;
    private List<Action> actionsActives;

    public BarreActions(Fenetre parent,
                        Collection<Action> actions,
                        int nombreMaximal,
                        Orientation orientation,
                        Justification justification,
                        Ajustement ajustement,
                        int espacement,
                        int abcisses,
                        int ordonnee,
                        int longueur) throws LwjglException {
        super(parent, false);
        this.desactivations = new LinkedList<>();
        this.orientation = orientation;
        this.justification = justification;
        this.ajustement = ajustement;
        this.espacement = espacement;
        this.abcisses = abcisses;
        this.ordonnee = ordonnee;
        this.longueur = longueur;
        this.actions = new ArrayList<>(actions);
        this.epaisseur = placerActions(nombreMaximal);
        this.actionsSurvolees = new ArrayList<>();
        this.actionsActives = new ArrayList<>();
    }

    private int placerActions(int nombreMaximal) {
        int longueurActions = actions.stream()
                .map(action -> (orientation == Orientation.HORIZONTAL ? action.largeur() : action.hauteur()))
                .reduce(0, Integer::sum);
        int epaisseurActions = actions.stream()
                .map(action -> (orientation == Orientation.HORIZONTAL ? action.hauteur() : action.largeur()))
                .reduce(0, Math::max);
        int espacementMaximal = (longueur - longueurActions) / (nombreMaximal - 1);
        int espacementReel = justification == Justification.CENTRAL ? espacementMaximal : Math.min(espacementMaximal, espacement);

        AtomicInteger longueurCourante = new AtomicInteger(0);
        if (orientation == Orientation.HORIZONTAL) {
            actions.forEach(action -> {
                int decalage = switch (ajustement) {
                    case DEBUT -> 0;
                    case CENTRAL -> (epaisseurActions - action.hauteur()) / 2;
                    case FIN -> epaisseurActions - action.hauteur();
                };
                action.placer(abcisses + longueurCourante.get(), ordonnee + decalage);
                longueurCourante.getAndAccumulate(espacementReel + action.largeur(), Integer::sum);
            });
        } else {
            actions.forEach(action -> {
                int decalage = switch (ajustement) {
                    case DEBUT -> 0;
                    case CENTRAL -> (epaisseurActions - action.largeur()) / 2;
                    case FIN -> epaisseurActions - action.largeur();
                };
                action.placer(abcisses + decalage, ordonnee + longueurCourante.get());
                longueurCourante.getAndAccumulate(espacementReel + action.hauteur(), Integer::sum);
            });
        }
        return epaisseurActions;
    }

    @Override
    public void produire(Vision vision) {
        super.produire(vision);
        actionsActives.removeIf(Action::ephemere);
    }

    @Override
    protected void dessiner() {
        nvgBeginPath(contexte);
        if (orientation == Orientation.HORIZONTAL) {
            nvgRect(contexte, abcisses, ordonnee, longueur, epaisseur);
        } else {
            nvgRect(contexte, abcisses, ordonnee, epaisseur, longueur);
        }
        nvgFillColor(contexte, AUBURN.nvg());
        nvgFill(contexte);
        nvgClosePath(contexte);

        actions.forEach(action -> {
            nvgBeginPath(contexte);
            nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
            nvgFillColor(contexte, BLANC.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            nvgBeginPath(contexte);
            nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
            nvgFillPaint(contexte, nvgImagePattern(contexte,
                    action.abscisse(),
                    action.ordonnee(),
                    action.largeur(),
                    action.hauteur(),
                    0,
                    action.image.nvg(),
                    1,
                    NVGPaint.create()));
            nvgFill(contexte);
            nvgClosePath(contexte);
        });

        actionsActives.stream().filter(action -> !actionsSurvolees.contains(action) && action.anime())
                .forEach(action -> {
                    nvgBeginPath(contexte);
                    nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
                    nvgFillColor(contexte, INDIGO_A50.nvg());
                    nvgFill(contexte);
                    nvgClosePath(contexte);
                });

        actionsSurvolees.stream().filter(Action::anime)
                .forEach(action -> {
                    nvgBeginPath(contexte);
                    nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
                    nvgFillColor(contexte, ROUGE_COQUELICOT_A50.nvg());
                    nvgFill(contexte);
                    nvgClosePath(contexte);
                });
    }

    @Override
    public boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (visible) {
            actionsSurvolees = actions.stream().filter(evenementSouris::inclus).collect(Collectors.toCollection(LinkedList::new));
        } else {
            actionsSurvolees.clear();
        }
        return !actionsSurvolees.isEmpty();
    }

    @Override
    public void desurvoler() {
        actionsSurvolees.clear();
    }

    @Override
    public void activer() {
        actionsActives = new ArrayList<>(actionsSurvolees);
        if (actionsActives.isEmpty()) {
            avertirDesactivations();
        } else {
            actionsActives.forEach(Activable::avertirActivations);
        }
    }

    @Override
    public void deactiver() {
        actionsActives.clear();
        avertirDesactivations();
    }

    @Override
    public Collection<Desactivation> desactivations() {
        return desactivations;
    }

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public enum Justification {
        DEBUT, CENTRAL
    }

    public enum Ajustement {
        DEBUT, CENTRAL, FIN
    }
}
