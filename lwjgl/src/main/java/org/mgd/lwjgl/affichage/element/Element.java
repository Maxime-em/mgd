package org.mgd.lwjgl.affichage.element;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.affichage.Primitif;
import org.mgd.lwjgl.affichage.Transition;
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
    private final LinkedList<Transition<Matrice<Float>, Float[]>> transitions;
    protected List<Forme> formes;
    protected List<Forme> formesSurvoles;
    protected List<Forme> formesActives;

    protected Element(Fenetre parent, String identifiant, float[] translation, float[] agrandissement, float[] rotation, Map<String, Path> textures) throws LwjglException {
        super(parent);
        this.activations = new LinkedList<>();
        this.desactivations = new LinkedList<>();
        this.transitions = new LinkedList<>();
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
        if (!transitions.isEmpty()) {
            transitions.getFirst().lineariser(ellipse);
            transitions.removeIf(Transition::finie);
        }
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

    public void translater(float decalagex, float decalagey, long duree) {
        Float[] depart = transitions.isEmpty()
                ? new Float[]{transformation.valeur(0, 3), transformation.valeur(1, 3), transformation.valeur(2, 3)}
                : transitions.getLast().arrive();
        Float[] arrivee = {depart[0] + decalagex, depart[1] + decalagey, depart[2]};
        transitions.addLast(new Transition<>(transformation, depart, arrivee, duree) {
            @Override
            protected Float[] multiplierParScalaire(Double scalaire, Float[] valeur) {
                return new Float[]{(float) (scalaire * valeur[0]), (float) (scalaire * valeur[1]), (float) (scalaire * valeur[2])};
            }

            @Override
            protected Float[] sommer(Float[] valeur1, Float[] valeur2) {
                return new Float[]{valeur1[0] + valeur2[0], valeur1[1] + valeur2[1], valeur1[2] + valeur2[2]};
            }

            @Override
            protected void appliquer(Matrice<Float> matrice, Float[] valeur) {
                matrice.modifierValeur(0, 3, valeur[0], (_, nouvelle) -> nouvelle);
                matrice.modifierValeur(1, 3, valeur[1], (_, nouvelle) -> nouvelle);
                matrice.modifierValeur(2, 3, valeur[2], (_, nouvelle) -> nouvelle);
            }
        });
    }

    public String identifiant() {
        return identifiant;
    }

    public float minimumz() {
        return formes.stream().map(Forme::minimumz).min(Float::compare).orElse(Float.MIN_VALUE);
    }
}
