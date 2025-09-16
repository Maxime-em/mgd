package org.mgd.lwjgl.element;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.forme.Forme;
import org.mgd.lwjgl.souscription.Activation;
import org.mgd.lwjgl.souscription.Desactivation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ElementActivable<A, D> extends Element implements Activable<Forme, A, D> {
    private final LinkedList<Activation<Forme, A>> activations;
    private final LinkedList<Desactivation<D>> desactivations;
    private List<Forme> formesSurvoles;
    private List<Forme> formesActives;

    public ElementActivable(Fenetre parent, String identifiant, float[] translation, float[] agrandissement, float[] rotation, Map<String, String> textures) throws LwjglException {
        super(parent, identifiant, translation, agrandissement, rotation, textures);
        this.activations = new LinkedList<>();
        this.desactivations = new LinkedList<>();
        this.formesSurvoles = new LinkedList<>();
        this.formesActives = new LinkedList<>();
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
            avertirDesactivations(desactivations);
        } else {
            formesActives.forEach(forme -> avertirActivations(forme, activations));
        }
    }

    @Override
    public void deactiver() {
        formesActives.forEach(Forme::deactiver);
        formesActives.clear();
        desactivations.forEach(desactivation -> desactivation.traiter(elementDesactivation()));
    }

    @Override
    public Collection<Activation<Forme, A>> activations() {
        return activations;
    }

    @Override
    public Collection<Desactivation<D>> desactivations() {
        return desactivations;
    }
}
