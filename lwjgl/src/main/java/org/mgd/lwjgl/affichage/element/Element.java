package org.mgd.lwjgl.affichage.element;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.affichage.Primitif;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.forme.Forme;
import org.mgd.lwjgl.interne.Ombreur;
import org.mgd.lwjgl.interne.Tisseur;
import org.mgd.lwjgl.souscription.Activable;
import org.mgd.lwjgl.souscription.Activation;
import org.mgd.lwjgl.souscription.Desactivable;
import org.mgd.lwjgl.souscription.Desactivation;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Element<A> extends Primitif implements Animateur, Activable<A>, Desactivable {
    private final Matrice<Float> transformation;
    private final String identifiant;
    private final LinkedList<Activation<A>> activations;
    private final LinkedList<Desactivation> desactivations;
    protected List<Forme> formes;
    protected List<Forme> formesSurvoles;
    protected List<Forme> formesActives;

    protected Element(Fenetre parent, String identifiant, float[] translation, float[] agrandissement, float[] rotation, Map<String, Path> textures) throws LwjglException {
        super(parent);
        this.activations = new LinkedList<>();
        this.desactivations = new LinkedList<>();
        this.formesSurvoles = new ArrayList<>();
        this.formesActives = new ArrayList<>();
        this.identifiant = identifiant;
        this.transformation = Matrice.transformation(translation, agrandissement, rotation);
        this.formes = Collections.emptyList();

        if (!textures.isEmpty()) {
            Tisseur.compiler(identifiant, textures);
        }
        parent.enfants().add(this);
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
    public void desactiver() {
        formesActives.forEach(Forme::deactiver);
        formesActives.clear();
        avertirDesactivations();
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public void jouer(long ellipse, Vision vision) {
        Ombreur.specifier("transformation", transformation);
        formes.forEach(Forme::produire);
    }

    @Override
    public Collection<Activation<A>> activations() {
        return activations;
    }

    @Override
    public Collection<Desactivation> desactivations() {
        return desactivations;
    }

    public void nettoyer() {
        formes.forEach(Forme::nettoyer);
    }

    public String identifiant() {
        return identifiant;
    }

    public float minimumz() {
        return formes.stream().map(Forme::minimumz).min(Float::compare).orElse(Float.MIN_VALUE);
    }
}
