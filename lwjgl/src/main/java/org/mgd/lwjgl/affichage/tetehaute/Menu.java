package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.affichage.tetehaute.composant.ActionTextutelle;
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
    private LinkedList<ActionTextutelle<?>> actionsSurvoles;

    public Menu(Fenetre parent, Collection<ActionTextutelle<?>> titres, Collection<ActionTextutelle<?>> textes) throws LwjglException {
        super(parent, true, true);
        this.identifiantPremierePage = UUID.randomUUID();
        this.pages = new HashMap<>();
        this.actionsSurvoles = new LinkedList<>();
        this.pageCourante = new Page(new ArrayList<>(titres), new ArrayList<>(textes));
        this.pages.put(this.identifiantPremierePage, pageCourante);

        placer(this.pageCourante);
    }

    public <T, U> void ajouterPage(ActionTextutelle<T> declencheur, Collection<ActionTextutelle<U>> textes, NVGPolice police) {
        Page page = new Page(Collections.emptyList(), new ArrayList<>(textes));
        pages.put(declencheur.uuid(), page);

        ActionTextutelle<Void> retour = new ActionTextutelle<>(24f, police, AffichageTeteHaute.BLANC, () -> "Retour");
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
        page.textes.forEach(action -> action.placer((parent.largeur() - action.largeur()) / 2, ordonneeCourante.getAndAccumulate(action.hauteur() + margeBoutons, Double::sum).intValue()));
    }

    private double hauteur(ActionTextutelle<?> actionTextutelle) {
        actionTextutelle.dimensionner(contexte);
        return actionTextutelle.hauteur();
    }

    private void dessiner(ActionTextutelle<?> actionTextutelle) {
        nvgFontSize(contexte, actionTextutelle.taille());
        nvgFontFace(contexte, actionTextutelle.police().identifiant());
        nvgFillColor(contexte, actionTextutelle.couleur().nvg());
        nvgText(contexte, actionTextutelle.abscisse(), actionTextutelle.ordonnee(), actionTextutelle.texte().get());
    }

    @Override
    public Fenetre parent() {
        return parent;
    }

    @Override
    public boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (visible) {
            actionsSurvoles = pageCourante.textes
                    .stream()
                    .filter(action -> evenementSouris.inclus(action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur()))
                    .collect(Collectors.toCollection(LinkedList::new));
        } else {
            actionsSurvoles.clear();
        }
        return !actionsSurvoles.isEmpty();
    }

    @Override
    public void retirer(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        actionsSurvoles.clear();
    }

    @Override
    public Collection<Identifiable> amorcer(boolean droite) {
        return actionsSurvoles.stream().map(Identifiable.class::cast).toList();
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

    private record Page(List<ActionTextutelle<?>> titres, List<ActionTextutelle<?>> textes) {
    }
}
