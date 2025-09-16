package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Activable;
import org.mgd.lwjgl.souscription.Desactivable;
import org.mgd.lwjgl.souscription.Desactivation;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.lwjgl.nanovg.NanoVG.*;

public class Menu extends AffichageTeteHaute implements Animateur, Desactivable {
    public static final String IDENTIFIANT_BOUTON_RETOUR = "Retour à la première page du menu";

    private final LinkedList<Desactivation> desactivations;
    private final List<Page<?>> pages;
    private final AtomicReference<Float> hauteurTitres = new AtomicReference<>();
    private final AtomicReference<Float> hauteurBoutons = new AtomicReference<>();
    private Page<?> pageCourante;
    private List<Bouton<?>> boutonsSurvoles;

    public Menu(Fenetre parent, Collection<Ecrit> titres, Collection<Bouton<Void>> boutons) throws LwjglException {
        super(parent, true);
        this.desactivations = new LinkedList<>();
        this.pages = new ArrayList<>();
        this.boutonsSurvoles = new ArrayList<>();
        this.pageCourante = new Page<>(new ArrayList<>(titres), new ArrayList<>(boutons));
        this.pages.add(pageCourante);
    }

    public <T, U> void ajouterPage(Bouton<T> bouton, Collection<Bouton<U>> boutons, NVGPolice police) {
        Page<U> page = new Page<>(Collections.emptyList(), new ArrayList<>(boutons));
        pages.add(page);

        Bouton<U> boutonRetour = new Bouton<>(IDENTIFIANT_BOUTON_RETOUR, 24f, police, AffichageTeteHaute.BLANC, () -> "Retour", new float[]{0f, 0f, 0f, 0f}, null);
        page.boutons.add(boutonRetour);

        boutonRetour.souscrire(objet -> pageCourante = pages.getFirst());
        bouton.souscrire(objet -> pageCourante = page);
    }

    public void retourPremierePage() {
        pageCourante = pages.getFirst();
    }

    @Override
    protected void dessiner() {
        nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        dessinerTitres();
        dessinerBoutons();
    }

    private void dessinerTitres() {
        hauteurTitres.set(0f);
        pageCourante.titres.forEach(titre -> dessiner(titre,
                (parent.largeur() - titre.dimensions[2]) / 2f + titre.marges[3],
                hauteurTitres.getAndAccumulate(titre.dimensions[3] + titre.marges[0] + titre.marges[2], Float::sum) + titre.marges[0]));
    }

    private void dessinerBoutons() {
        hauteurBoutons.set(hauteurTitres.get());
        pageCourante.boutons.forEach(bouton -> dessiner(bouton,
                (parent.largeur() - bouton.dimensions[2]) / 2f + bouton.marges[3],
                hauteurBoutons.getAndAccumulate(bouton.dimensions[3] + bouton.marges[0] + bouton.marges[2], Float::sum)));
    }

    @Override
    public boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (visible) {
            boutonsSurvoles = pageCourante.boutons.stream().filter(evenementSouris::inclus).collect(Collectors.toCollection(LinkedList::new));
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
            avertirDesactivations();
        } else {
            boutonsSurvoles.forEach(Activable::avertirActivations);
        }
    }

    @Override
    public void desactiver() {
        // Rien à faire
    }

    @Override
    public Collection<Desactivation> desactivations() {
        return desactivations;
    }

    private record Page<T>(List<Ecrit> titres, List<Bouton<T>> boutons) {
    }
}
