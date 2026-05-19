package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.affichage.tetehaute.composant.Ecrit;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGPolice;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Identifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.lwjgl.nanovg.NanoVG.*;

public class Menu extends AffichageTeteHaute implements Animateur {
    public static final double PROPORTION_HAUTEUR_TITRE = 0.3;
    public static final double PROPORTION_HAUTEUR_BOUTONS = 0.3;

    private final UUID identifiantPremierePage;
    private final Map<UUID, Page> pages;
    private Page pageCourante;
    private LinkedList<Ecrit<?>> ecritsSurvoles;

    public Menu(Fenetre parent, Collection<Ecrit<?>> titres, Collection<Ecrit<?>> textes) throws LwjglException {
        super(parent, true, true);
        this.identifiantPremierePage = UUID.randomUUID();
        this.pages = new HashMap<>();
        this.ecritsSurvoles = new LinkedList<>();
        this.pageCourante = new Page(new ArrayList<>(titres), new ArrayList<>(textes));
        this.pages.put(this.identifiantPremierePage, pageCourante);

        placer(this.pageCourante);
    }

    public <T, U> void ajouterPage(Ecrit<T> declencheur, Collection<Ecrit<U>> textes, NVGPolice police) {
        Page page = new Page(Collections.emptyList(), new ArrayList<>(textes));
        pages.put(declencheur.uuid(), page);

        Ecrit<Void> retour = new Ecrit<>(24f, police, AffichageTeteHaute.BLANC, () -> "Retour");
        page.textes.add(retour);
        pages.put(retour.uuid(), pages.get(this.identifiantPremierePage));

        placer(page);
    }

    public void premierePage() {
        pageCourante = pages.get(this.identifiantPremierePage);
    }

    private void placer(Page page) {
        nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

        double hauteurTitres = page.titres.stream().mapToDouble(this::hauteur).sum();
        double hauteurBoutons = page.textes.stream().mapToDouble(this::hauteur).sum();
        double restant = Math.max(parent.hauteur() - hauteurTitres - hauteurBoutons, 0);
        double margeTitres = PROPORTION_HAUTEUR_TITRE * restant / (page.titres.size() + 1);
        double margeBoutons = PROPORTION_HAUTEUR_BOUTONS * restant / (page.textes.size() + 1);
        double interligne = (1 - PROPORTION_HAUTEUR_TITRE - PROPORTION_HAUTEUR_BOUTONS) * restant / 3;

        AtomicReference<Double> ordonneeCourante = new AtomicReference<>(margeTitres);
        page.titres.forEach(titre -> titre.placer((parent.largeur() - titre.largeur()) / 2, ordonneeCourante.getAndAccumulate(titre.hauteur() + margeTitres, Double::sum).intValue()));
        ordonneeCourante.getAndAccumulate(interligne, Double::sum);
        page.textes.forEach(ecrit -> ecrit.placer((parent.largeur() - ecrit.largeur()) / 2, ordonneeCourante.getAndAccumulate(ecrit.hauteur() + margeBoutons, Double::sum).intValue()));
    }

    private double hauteur(Ecrit<?> ecrit) {
        return ecrit.dimensionner(contexte).hauteur();
    }

    private void dessiner(Ecrit<?> ecrit) {
        nvgFontSize(contexte, ecrit.taille());
        nvgFontFace(contexte, ecrit.police().identifiant());
        nvgFillColor(contexte, ecrit.couleur().nvg());
        nvgText(contexte, ecrit.abscisse(), ecrit.ordonnee(), ecrit.texte().get());
    }

    @Override
    public Fenetre parent() {
        return parent;
    }

    @Override
    public boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (visible) {
            ecritsSurvoles = pageCourante.textes
                    .stream()
                    .filter(ecrit -> evenementSouris.inclus(ecrit.abscisse(), ecrit.ordonnee(), ecrit.largeur(), ecrit.hauteur()))
                    .collect(Collectors.toCollection(LinkedList::new));
        } else {
            ecritsSurvoles.clear();
        }
        return !ecritsSurvoles.isEmpty();
    }

    @Override
    public void retirer(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        ecritsSurvoles.clear();
    }

    @Override
    public Collection<Identifiable> amorcer(boolean droite) {
        return ecritsSurvoles.stream().map(Identifiable.class::cast).toList();
    }

    @Override
    protected void dessiner(long ellipse) {
        nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        pageCourante.titres.forEach(this::dessiner);
        pageCourante.textes.forEach(this::dessiner);
    }

    @Override
    public void maj(Vision vision, Fenetre.EvenementSouris evenementSouris, EvenementAmorcages evenementAmorcagesCourant) throws LwjglException {
        Animateur.super.maj(vision, evenementSouris, evenementAmorcagesCourant);
        evenementAmorcagesCourant.amorcages()
                .stream()
                .filter(amorcage -> !amorcage.droite() && pages.containsKey(amorcage.uuid()))
                .forEach(amorcage -> pageCourante = pages.get(amorcage.uuid()));
    }

    private record Page(List<Ecrit<?>> titres, List<Ecrit<?>> textes) {
    }
}
