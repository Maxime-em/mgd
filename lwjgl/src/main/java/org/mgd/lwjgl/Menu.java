package org.mgd.lwjgl;

import org.mgd.lwjgl.element.Activable;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Activation;
import org.mgd.lwjgl.souscription.Desactivation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.lwjgl.nanovg.NanoVG.*;

public class Menu extends AffichageTeteHaute implements Activable<AffichageTeteHaute.Bouton, String, Void> {
    private final List<Ecrit> titres;
    private final List<Bouton> boutons;
    private final AtomicReference<Float> hauteurTitres = new AtomicReference<>();
    private final AtomicReference<Float> hauteurBoutons = new AtomicReference<>();
    private final LinkedList<Activation<Bouton, String>> activations;
    private final LinkedList<Desactivation<Void>> desactivations;
    private List<Bouton> boutonsSurvoles;

    public Menu(Fenetre parent) throws LwjglException {
        super(parent, true);
        this.titres = new ArrayList<>();
        this.boutons = new LinkedList<>();
        this.boutonsSurvoles = new LinkedList<>();
        this.activations = new LinkedList<>();
        this.desactivations = new LinkedList<>();
    }

    public void ajouteTitre(Ecrit ecrit) {
        titres.add(ecrit);
    }

    public void ajouteBouton(Bouton bouton) {
        boutons.add(bouton);
    }

    @Override
    protected void dessiner() {
        nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        dessinerTitres();
        dessinerBoutons();
    }

    private void dessinerTitres() {
        hauteurTitres.set(0f);
        titres.forEach(titre -> dessiner(titre,
                (parent.largeur() - titre.dimensions[2]) / 2f + titre.marges[3],
                hauteurTitres.getAndAccumulate(titre.dimensions[3] + titre.marges[0] + titre.marges[2], Float::sum) + titre.marges[0]));
    }

    private void dessinerBoutons() {
        hauteurBoutons.set(hauteurTitres.get());
        boutons.forEach(bouton -> dessiner(bouton,
                (parent.largeur() - bouton.dimensions[2]) / 2f + bouton.marges[3],
                hauteurBoutons.getAndAccumulate(bouton.dimensions[3] + bouton.marges[0] + bouton.marges[2], Float::sum)));
    }

    @Override
    public boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (visible) {
            boutonsSurvoles = boutons.stream().filter(evenementSouris::inclus).collect(Collectors.toCollection(LinkedList::new));
        } else {
            boutonsSurvoles.clear();
        }
        return !boutonsSurvoles.isEmpty();
    }

    @Override
    public void desurvoler() {
        boutonsSurvoles.clear();
    }

    @Override
    public void activer() {
        if (boutonsSurvoles.isEmpty()) {
            avertirDesactivations(desactivations);
        } else {
            boutonsSurvoles.forEach(bouton -> avertirActivations(bouton, activations));
        }
    }

    @Override
    public void deactiver() {
        // Rien à faire
    }

    @Override
    public String elementActivation(Bouton parent) {
        return parent.identifiant;
    }

    @Override
    public Void elementDesactivation() {
        return null;
    }

    @Override
    public Collection<Activation<Bouton, String>> activations() {
        return activations;
    }

    @Override
    public Collection<Desactivation<Void>> desactivations() {
        return desactivations;
    }
}
