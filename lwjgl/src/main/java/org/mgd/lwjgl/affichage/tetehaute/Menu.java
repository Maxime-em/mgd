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
    public static final double PROPORTION_HAUTEUR_TITRE = 0.3;
    public static final double PROPORTION_HAUTEUR_BOUTONS = 0.3;

    private final LinkedList<Desactivation> desactivations;
    private final List<Page<?>> pages;
    private Page<?> pageCourante;
    private List<Bouton<?>> boutonsSurvoles;

    public Menu(Fenetre parent, Collection<Ecrit> titres, Collection<Bouton<Void>> boutons) throws LwjglException {
        super(parent, true);
        this.desactivations = new LinkedList<>();
        this.pages = new ArrayList<>();
        this.boutonsSurvoles = new ArrayList<>();
        this.pageCourante = new Page<>(new ArrayList<>(titres), new ArrayList<>(boutons));
        this.pages.add(pageCourante);

        placer(this.pageCourante);
    }

    public <T, U> void ajouterPage(Bouton<T> bouton, Collection<Bouton<U>> boutons, NVGPolice police) {
        Page<U> page = new Page<>(Collections.emptyList(), new ArrayList<>(boutons));
        pages.add(page);

        Bouton<U> boutonRetour = new Bouton<>(IDENTIFIANT_BOUTON_RETOUR, 24f, police, AffichageTeteHaute.BLANC, () -> "Retour", null);
        page.boutons.add(boutonRetour);

        boutonRetour.souscrire(_ -> pageCourante = pages.getFirst());
        bouton.souscrire(_ -> pageCourante = page);

        placer(page);
    }

    public void retourPremierePage() {
        pageCourante = pages.getFirst();
    }

    private void placer(Page<?> page) {
        nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

        double hauteurTitres = page.titres.stream().mapToDouble(this::hauteur).sum();
        double hauteurBoutons = page.boutons.stream().mapToDouble(this::hauteur).sum();
        double restant = Math.max(parent.hauteur() - hauteurTitres - hauteurBoutons, 0);
        double margeTitres = PROPORTION_HAUTEUR_TITRE * restant / (page.titres.size() + 1);
        double margeBoutons = PROPORTION_HAUTEUR_BOUTONS * restant / (page.boutons.size() + 1);
        double interligne = (1 - PROPORTION_HAUTEUR_TITRE - PROPORTION_HAUTEUR_BOUTONS) * restant / 3;

        AtomicReference<Double> positiony = new AtomicReference<>(margeTitres);
        page.titres.forEach(titre -> {
            titre.position[0] = (parent.largeur() - titre.dimensions[2]) / 2f;
            titre.position[1] = positiony.getAndAccumulate(titre.dimensions[3] + margeTitres, Double::sum).floatValue();
        });
        positiony.getAndAccumulate(interligne, Double::sum);
        page.boutons.forEach(bouton -> {
            bouton.position[0] = (parent.largeur() - bouton.dimensions[2]) / 2f;
            bouton.position[1] = positiony.getAndAccumulate(bouton.dimensions[3] + margeBoutons, Double::sum).floatValue();
        });
    }

    private double hauteur(Ecrit ecrit) {
        nvgFontSize(contexte, ecrit.taille);
        nvgFontFace(contexte, ecrit.police.identifiant());
        nvgTextBounds(contexte, 0f, 0f, ecrit.texte.get(), ecrit.dimensions);
        return ecrit.dimensions[3];
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

    @Override
    protected void dessiner(long ellipse) {
        nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        pageCourante.titres.forEach(this::dessiner);
        pageCourante.boutons.forEach(this::dessiner);
    }

    private void dessiner(Ecrit ecrit) {
        nvgFontSize(contexte, ecrit.taille);
        nvgFontFace(contexte, ecrit.police.identifiant());
        nvgFillColor(contexte, ecrit.couleur.nvg());
        nvgText(contexte, ecrit.position[0], ecrit.position[1], ecrit.texte.get());
    }

    private record Page<T>(List<Ecrit> titres, List<Bouton<T>> boutons) {
    }
}
