package org.mgd.lwjgl.affichage.element;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.forme.Forme;
import org.mgd.lwjgl.souscription.Activable;
import org.mgd.lwjgl.souscription.Activation;
import org.mgd.lwjgl.souscription.Desactivable;
import org.mgd.lwjgl.souscription.Desactivation;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ElementAnimateur<A> extends Element implements Animateur, Activable<A>, Desactivable {
    private final LinkedList<Activation<A>> activations;
    private final LinkedList<Desactivation> desactivations;
    protected List<Forme> formesSurvoles;
    protected List<Forme> formesActives;

    public ElementAnimateur(Fenetre parent, String identifiant, float[] translation, float[] agrandissement, float[] rotation, Map<String, Path> textures) throws LwjglException {
        super(parent, identifiant, translation, agrandissement, rotation, textures);
        this.activations = new LinkedList<>();
        this.desactivations = new LinkedList<>();
        this.formesSurvoles = new ArrayList<>();
        this.formesActives = new ArrayList<>();
    }

    @Override
    public boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (visible) {
            formesSurvoles = formes.stream().filter(forme -> forme.survoler(vision, evenementSouris, transformation)).collect(Collectors.toCollection(LinkedList::new));
        } else {
            formesSurvoles.clear();
        }
        return !formesSurvoles.isEmpty();
    }

    @Override
    public void desurvoler() {
        formesSurvoles.forEach(Forme::desurvoler);
        formesSurvoles.clear();
    }

    @Override
    public void activer() {
        formesActives.stream().filter(forme -> !forme.survole()).forEach(Forme::deactiver);
        formesSurvoles.forEach(Forme::basculer);
        formesActives = formesSurvoles.stream().filter(Forme::active).collect(Collectors.toCollection(LinkedList::new));
        if (formesActives.isEmpty()) {
            avertirDesactivations();
        } else {
            avertirActivations();
        }
    }

    @Override
    public void deactiver() {
        formesActives.forEach(Forme::deactiver);
        formesActives.clear();
        avertirDesactivations();
    }

    @Override
    public Collection<Activation<A>> activations() {
        return activations;
    }

    @Override
    public Collection<Desactivation> desactivations() {
        return desactivations;
    }
}
