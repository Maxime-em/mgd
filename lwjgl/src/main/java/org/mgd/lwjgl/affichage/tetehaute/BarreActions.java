package org.mgd.lwjgl.affichage.tetehaute;

import org.lwjgl.nanovg.NVGPaint;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.affichage.tetehaute.composant.ActionImagee;
import org.mgd.lwjgl.affichage.tetehaute.composant.ActionTextutelle;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Identifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.nanovg.NanoVG.*;

public class BarreActions<G> extends AffichageTeteHaute implements Animateur {
    private static final int MARGE_INFORMATIONS = 10;
    private static final Map<UUID, List<ActionTextutelle<Void>>> informations = new HashMap<>();
    private static final Map<UUID, Integer> abscisseActions = new HashMap<>();
    private static final Map<UUID, Integer> ordonneeActions = new HashMap<>();
    private static final Map<UUID, Integer> largeurActions = new HashMap<>();
    private static final Map<UUID, Integer> hauteurActions = new HashMap<>();

    private final Disposition disposition;
    private final int abcisses;
    private final int ordonnee;
    private final int longueur;
    private final Map<G, List<ActionImagee<?>>> groupes;
    private final List<ActionImagee<?>> actionsSurvolees;
    private final List<ActionImagee<?>> actionsLiees;
    private int epaisseurActions;
    private G groupe;

    public BarreActions(Fenetre parent, Disposition disposition, int abcisses, int ordonnee, int longueur) throws LwjglException {
        super(parent, false, true);
        this.disposition = disposition;
        this.abcisses = abcisses;
        this.ordonnee = ordonnee;
        this.longueur = longueur;
        this.groupes = new HashMap<>();
        this.epaisseurActions = 0;
        this.actionsSurvolees = new LinkedList<>();
        this.actionsLiees = new LinkedList<>();
    }

    private void placerActions() {
        List<ActionImagee<?>> actionImagees = groupes.getOrDefault(groupe, Collections.emptyList());
        int longueurActions = actionImagees.stream()
                .mapToInt(actionImagee -> disposition.orientation() == Disposition.Orientation.HORIZONTAL ? actionImagee.largeur() : actionImagee.hauteur())
                .sum();
        epaisseurActions = actionImagees.stream()
                .mapToInt(actionImagee -> disposition.orientation() == Disposition.Orientation.HORIZONTAL ? actionImagee.hauteur() : actionImagee.largeur())
                .max()
                .orElse(0);
        int espacementMaximal = actionImagees.size() > 1 ? (longueur - longueurActions) / (actionImagees.size() - 1) : 0;
        int espacementReel = disposition.justification() == Disposition.Justification.CENTRAL ? espacementMaximal : Math.min(espacementMaximal, disposition.espacement());

        AtomicInteger longueurCourante = new AtomicInteger(0);
        if (disposition.orientation() == Disposition.Orientation.HORIZONTAL) {
            actionImagees.forEach(actionImagee -> {
                int decalage = switch (disposition.alignement()) {
                    case DEBUT -> 0;
                    case CENTRAL -> (epaisseurActions - actionImagee.hauteur()) / 2;
                    case FIN -> epaisseurActions - actionImagee.hauteur();
                };
                actionImagee.placer(abcisses + longueurCourante.get(), ordonnee + decalage);
                longueurCourante.getAndAdd(espacementReel + actionImagee.largeur());
            });
        } else {
            actionImagees.forEach(actionImagee -> {
                int decalage = switch (disposition.alignement()) {
                    case DEBUT -> 0;
                    case CENTRAL -> (epaisseurActions - actionImagee.largeur()) / 2;
                    case FIN -> epaisseurActions - actionImagee.largeur();
                };
                actionImagee.placer(abcisses + decalage, ordonnee + longueurCourante.get());
                longueurCourante.getAndAdd(espacementReel + actionImagee.hauteur());
            });
        }
    }

    private void placerInformations() {
        List<ActionImagee<?>> actionImagees = groupes.getOrDefault(groupe, Collections.emptyList());
        actionImagees.forEach(actionImagee -> {
            List<ActionTextutelle<Void>> actionTextutelles = informations.getOrDefault(actionImagee.uuid(), Collections.emptyList());
            int largeur = actionTextutelles.stream().mapToInt(element -> element.dimensionner(contexte).largeur()).max().orElse(0);
            int hauteur = actionTextutelles.stream().mapToInt(ActionTextutelle::hauteur).sum();
            int abscisseCourante = switch (disposition.position()) {
                case HAUT, BAS -> actionImagee.abscisse();
                case DROITE -> abcisses + epaisseurActions + MARGE_INFORMATIONS;
                case GAUCHE -> abcisses - largeur - MARGE_INFORMATIONS;
            };
            AtomicInteger ordonneeCourante = new AtomicInteger(switch (disposition.position()) {
                case HAUT -> ordonnee - hauteur - MARGE_INFORMATIONS;
                case BAS -> ordonnee + epaisseurActions + MARGE_INFORMATIONS;
                case DROITE, GAUCHE -> actionImagee.ordonnee();
            });
            abscisseActions.put(actionImagee.uuid(), abscisseCourante);
            ordonneeActions.put(actionImagee.uuid(), ordonneeCourante.get());
            largeurActions.put(actionImagee.uuid(), largeur);
            hauteurActions.put(actionImagee.uuid(), hauteur);
            actionTextutelles.forEach(element -> element.placer(abscisseCourante, ordonneeCourante.getAndAdd(element.hauteur())));
        });
    }

    private void dessinerInfobulle(ActionImagee<?> actionImagee) {
        List<ActionTextutelle<Void>> actionTextutelles = informations.getOrDefault(actionImagee.uuid(), Collections.emptyList());
        if (!actionTextutelles.isEmpty()) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

            nvgBeginPath(contexte);
            nvgRect(contexte,
                    abscisseActions.getOrDefault(actionImagee.uuid(), 0),
                    ordonneeActions.getOrDefault(actionImagee.uuid(), 0),
                    largeurActions.getOrDefault(actionImagee.uuid(), 0),
                    hauteurActions.getOrDefault(actionImagee.uuid(), 0));
            nvgFillColor(contexte, EMERAUDE.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            actionTextutelles.forEach(element -> {
                nvgFontSize(contexte, element.taille());
                nvgFontFace(contexte, element.police().identifiant());
                nvgFillColor(contexte, element.couleur().nvg());
                nvgText(contexte, element.abscisse(), element.ordonnee(), element.texte().get());
            });
        }
    }

    @Override
    public Fenetre parent() {
        return parent;
    }

    @Override
    public void maj(Vision vision, EvenementSouris evenementSouris, Fenetre.EvenementAmorcages evenementAmorcagesCourant) throws LwjglException {
        Animateur.super.maj(vision, evenementSouris, evenementAmorcagesCourant);
        actionsLiees.addAll(
                groupes.getOrDefault(groupe, Collections.emptyList())
                        .stream()
                        .filter(actionImagee -> actionImagee.liaisons().stream().anyMatch(liaison -> liaison.visible() && liaison.survoler(vision, evenementSouris)))
                        .toList());
    }

    @Override
    public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
        actionsSurvolees.clear();
        actionsLiees.clear();
        if (visible) {
            actionsSurvolees.addAll(
                    groupes.getOrDefault(groupe, Collections.emptyList())
                            .stream()
                            .filter(actionImagee -> actionImagee.survoler(vision, evenementSouris))
                            .toList());
        }
        return !actionsSurvolees.isEmpty();
    }

    @Override
    public void retirer(Vision vision, EvenementSouris evenementSouris) {
        actionsSurvolees.clear();
        actionsLiees.clear();
    }

    @Override
    public Collection<Identifiable> amorcer(boolean droite) {
        return actionsSurvolees.stream().map(Identifiable.class::cast).toList();
    }

    @Override
    protected void dessiner(long ellipse) {
        nvgBeginPath(contexte);
        if (disposition.orientation() == Disposition.Orientation.HORIZONTAL) {
            nvgRect(contexte, abcisses, ordonnee, longueur, epaisseurActions);
        } else {
            nvgRect(contexte, abcisses, ordonnee, epaisseurActions, longueur);
        }
        nvgFillColor(contexte, AUBURN.nvg());
        nvgFill(contexte);
        nvgClosePath(contexte);

        groupes.getOrDefault(groupe, Collections.emptyList()).forEach(actionImagee -> {
            nvgBeginPath(contexte);
            nvgRect(contexte, actionImagee.abscisse(), actionImagee.ordonnee(), actionImagee.largeur(), actionImagee.hauteur());
            nvgFillColor(contexte, BLANC.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            nvgBeginPath(contexte);
            nvgRect(contexte, actionImagee.abscisse(), actionImagee.ordonnee(), actionImagee.largeur(), actionImagee.hauteur());
            nvgFillPaint(contexte, nvgImagePattern(contexte,
                    actionImagee.abscisse(),
                    actionImagee.ordonnee(),
                    actionImagee.largeur(),
                    actionImagee.hauteur(),
                    0,
                    actionImagee.image().nvg(),
                    1,
                    NVGPaint.create()));
            nvgFill(contexte);
            nvgClosePath(contexte);

            if (actionImagee.active() && actionImagee.anime()) {
                nvgBeginPath(contexte);
                nvgRect(contexte, actionImagee.abscisse(), actionImagee.ordonnee(), actionImagee.largeur(), actionImagee.hauteur());
                nvgFillColor(contexte, INDIGO_A50.nvg());
                nvgFill(contexte);
                nvgClosePath(contexte);
            }
        });

        actionsSurvolees.forEach(actionImagee -> {
            nvgBeginPath(contexte);
            nvgRect(contexte, actionImagee.abscisse(), actionImagee.ordonnee(), actionImagee.largeur(), actionImagee.hauteur());
            nvgFillColor(contexte, ROUGE_COQUELICOT_A50.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            dessinerInfobulle(actionImagee);
        });

        actionsLiees.forEach(this::dessinerInfobulle);
    }

    public <T> void ajouter(G groupe, ActionImagee<T> actionImagee) {
        groupes.computeIfAbsent(groupe, _ -> new ArrayList<>()).add(actionImagee);
    }

    @SafeVarargs
    public final <T> void ajouter(G groupe, ActionImagee<T>... actionImagees) {
        groupes.computeIfAbsent(groupe, _ -> new ArrayList<>()).addAll(List.of(actionImagees));
    }

    public void ajouter(UUID uuid, ActionTextutelle<Void> nouveau) {
        informations.computeIfAbsent(uuid, _ -> new LinkedList<>()).add(nouveau);
    }

    public void afficher(G groupe) {
        if (!Objects.equals(this.groupe, groupe)) {
            groupes.getOrDefault(this.groupe, Collections.emptyList()).forEach(ActionImagee::masquer);
            groupes.getOrDefault(groupe, Collections.emptyList()).forEach(ActionImagee::afficher);
            this.groupe = groupe;
            placerActions();
            placerInformations();
        }
    }

    public void desactiverActions() {
        groupes.values().stream().flatMap(Collection::stream).forEach(ActionImagee::desactiver);
    }
}
