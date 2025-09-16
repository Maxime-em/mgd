package org.mgd.lwjgl.affichage.tetehaute;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.affichage.Transition;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Desactivable;
import org.mgd.lwjgl.souscription.Desactivation;

import java.util.*;
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
    private final Map<Action, Transition<Action, Float>> transitions;
    private final Map<Object, List<Action>> actionsDediees;
    private final List<Action> actionsParDefaut;
    private List<Action> actions;
    private List<Action> actionsSurvolees;
    private List<Action> actionsActives;
    private boolean fige;

    public BarreActions(Fenetre parent,
                        Collection<Action> actions,
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
        this.transitions = new HashMap<>();
        this.actionsDediees = new HashMap<>();
        this.actionsParDefaut = new ArrayList<>(actions);
        this.actions = this.actionsParDefaut;
        this.epaisseur = placerActions();
        this.actionsSurvolees = new ArrayList<>();
        this.actionsActives = new ArrayList<>();
    }

    private int placerActions() {
        int longueurActions = actions.stream()
                .mapToInt(action -> (orientation == Orientation.HORIZONTAL ? action.largeur() : action.hauteur()))
                .sum();
        int epaisseurActions = actions.stream()
                .mapToInt(action -> (orientation == Orientation.HORIZONTAL ? action.hauteur() : action.largeur()))
                .max()
                .orElse(0);
        int espacementMaximal = actions.size() > 1 ? (longueur - longueurActions) / (actions.size() - 1) : 0;
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
    public void jouer(long ellipse, Vision vision) {
        super.jouer(ellipse, vision);
        actionsActives.removeIf(action -> action.ephemere() && !transitions.containsKey(action));
    }

    @Override
    protected void dessiner(long ellipse) {
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

        transitions.entrySet()
                .stream()
                .filter(element -> element.getKey().anime() && element.getKey().ephemere())
                .forEach(element -> {
                    Action action = element.getKey();
                    Transition<Action, Float> transition = element.getValue();
                    transition.lineariser(ellipse);

                    nvgBeginPath(contexte);
                    nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
                    nvgFillColor(contexte, action.couleur().nvg());
                    nvgFill(contexte);
                    nvgClosePath(contexte);
                });
        transitions.entrySet().removeIf(element -> element.getValue().finie());

        actionsActives.stream()
                .filter(action -> action.anime() && !action.ephemere() && !actionsSurvolees.contains(action))
                .forEach(action -> {
                    nvgBeginPath(contexte);
                    nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
                    nvgFillColor(contexte, INDIGO_A50.nvg());
                    nvgFill(contexte);
                    nvgClosePath(contexte);
                });

        actionsSurvolees.stream()
                .filter(Action::anime)
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
        if (!fige) {
            actionsActives = new ArrayList<>(actionsSurvolees);
            if (actionsActives.isEmpty()) {
                avertirDesactivations();
            } else {
                actionsActives.forEach(action -> {
                    Transition<Action, Float> transition = new Transition<>(action, 1f, 0f, 1_000) {
                        @Override
                        protected Float multiplierParScalaire(Double scalaire, Float valeur) {
                            return (float) (scalaire * valeur);
                        }

                        @Override
                        protected Float sommer(Float valeur1, Float valeur2) {
                            return valeur1 + valeur2;
                        }

                        @Override
                        protected void appliquer(Action action, Float valeur) {
                            NVGColor nvg = NVGColor.create();
                            nvg.r(0f);
                            nvg.g(0f);
                            nvg.b(0f);
                            nvg.a(valeur);
                            action.coloriser(new NVGCouleur("Couleur de transition", nvg));
                        }
                    };
                    transitions.put(action, transition);
                    action.avertirActivations();
                });
            }
        }
    }

    @Override
    public void desactiver() {
        if (!fige) {
            actionsActives.clear();
            avertirDesactivations();
        }
    }

    @Override
    public Collection<Desactivation> desactivations() {
        return desactivations;
    }

    public <T> void affilier(T objet, Action action) {
        actionsDediees.computeIfAbsent(objet, _ -> new ArrayList<>()).add(action);
    }

    public <T> void affilier(T objet, Action... actions) {
        actionsDediees.computeIfAbsent(objet, _ -> new ArrayList<>()).addAll(List.of(actions));
    }

    public <T> void exposer(T objet) {
        actions = actionsDediees.getOrDefault(objet, actionsParDefaut);
        placerActions();
    }

    public <T> void cacher(T objet) {
        if (actions == actionsDediees.getOrDefault(objet, actionsParDefaut)) {
            actions = actionsParDefaut;
            placerActions();
        }
    }

    public void figer() {
        fige = true;
    }

    public void defiger() {
        fige = false;
    }

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public enum Justification {
        DEBUT, CENTRAL, ETENDU
    }

    public enum Ajustement {
        DEBUT, CENTRAL, FIN
    }
}
