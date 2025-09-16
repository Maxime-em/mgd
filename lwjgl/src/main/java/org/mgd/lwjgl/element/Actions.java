package org.mgd.lwjgl.element;

import org.lwjgl.nanovg.NVGPaint;
import org.mgd.lwjgl.AffichageTeteHaute;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Activation;
import org.mgd.lwjgl.souscription.Desactivation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.lwjgl.nanovg.NanoVG.*;

public class Actions extends AffichageTeteHaute implements Activable<AffichageTeteHaute.Action, Integer, Void> {
    private final LinkedList<Activation<Action, Integer>> activations;
    private final LinkedList<Desactivation<Void>> desactivations;
    private final int decalage;
    private final List<Action> actions;
    private List<Action> actionsSurvoles;

    public Actions(Fenetre parent, int nombre, int largeur, int hauteur, int[] marges, NVGImage image) throws LwjglException {
        super(parent, false);
        this.activations = new LinkedList<>();
        this.desactivations = new LinkedList<>();
        this.decalage = (parent.largeur() - marges[1] - marges[3] - nombre * largeur) / (nombre - 1);
        this.actions = IntStream.range(0, nombre)
                .mapToObj(index ->
                        new Action(index,
                                marges[3] + index * (largeur + decalage),
                                parent.hauteur() - marges[2] - hauteur,
                                largeur,
                                hauteur,
                                image))
                .collect(Collectors.toCollection(LinkedList::new));
        this.actionsSurvoles = new LinkedList<>();
    }

    @Override
    protected void dessiner() {
        actions.forEach(action -> {
            nvgBeginPath(contexte);
            nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
            nvgFillPaint(contexte, nvgImagePattern(contexte,
                    action.abscisse() - action.index() * action.largeur(),
                    action.ordonnee(),
                    action.image().largeur(),
                    action.image().hauteur(),
                    0,
                    action.image().nvg(),
                    1,
                    NVGPaint.create()));
            nvgFill(contexte);
        });
    }

    @Override
    public boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (visible) {
            actionsSurvoles = actions.stream().filter(evenementSouris::inclus).collect(Collectors.toCollection(LinkedList::new));
        } else {
            actionsSurvoles.clear();
        }
        return !actionsSurvoles.isEmpty();
    }

    @Override
    public void desurvoler() {
        actionsSurvoles.clear();
    }

    @Override
    public void activer() {
        if (actionsSurvoles.isEmpty()) {
            avertirDesactivations(desactivations);
        } else {
            actionsSurvoles.forEach(bouton -> avertirActivations(bouton, activations));
        }
    }

    @Override
    public void deactiver() {
        // Rien à faire
    }

    @Override
    public Integer elementActivation(Action parent) {
        return parent.index();
    }

    @Override
    public Void elementDesactivation() {
        return null;
    }

    @Override
    public Collection<Activation<Action, Integer>> activations() {
        return activations;
    }

    @Override
    public Collection<Desactivation<Void>> desactivations() {
        return desactivations;
    }
}
