package org.mgd.lwjgl.affichage.tetehaute;

import org.lwjgl.nanovg.NVGPaint;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Amorcable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.nanovg.NanoVG.*;

public class BarreActions<G> extends AffichageTeteHaute implements Animateur, Amorcable<AffichageTeteHaute.Action<?>> {
    private static final int MARGE_INFORMATIONS = 10;
    private static final Map<UUID, List<Ecrit<Void>>> informations = new HashMap<>();
    private static final Map<UUID, Integer> abscisseEcrits = new HashMap<>();
    private static final Map<UUID, Integer> ordonneeEcrits = new HashMap<>();
    private static final Map<UUID, Integer> largeurEcrits = new HashMap<>();
    private static final Map<UUID, Integer> hauteurEcrits = new HashMap<>();

    private final Disposition disposition;
    private final int abcisses;
    private final int ordonnee;
    private final int longueur;
    private final Map<G, List<Action<?>>> groupes;
    private int epaisseurActions;
    private G groupe;
    private List<Action<?>> actions;
    private List<Action<?>> actionsSurvolees;
    private List<Action<?>> actionsLiees;

    public BarreActions(Fenetre parent, Disposition disposition, int abcisses, int ordonnee, int longueur) throws LwjglException {
        super(parent, false);
        this.disposition = disposition;
        this.abcisses = abcisses;
        this.ordonnee = ordonnee;
        this.longueur = longueur;
        this.groupes = new HashMap<>();
        this.actions = Collections.emptyList();
        this.epaisseurActions = 0;
        this.actionsSurvolees = new LinkedList<>();
    }

    private void placerActions() {
        int longueurActions = actions.stream()
                .mapToInt(action -> disposition.orientation() == Disposition.Orientation.HORIZONTAL ? action.largeur() : action.hauteur())
                .sum();
        epaisseurActions = actions.stream()
                .mapToInt(action -> disposition.orientation() == Disposition.Orientation.HORIZONTAL ? action.hauteur() : action.largeur())
                .max()
                .orElse(0);
        int espacementMaximal = actions.size() > 1 ? (longueur - longueurActions) / (actions.size() - 1) : 0;
        int espacementReel = disposition.justification() == Disposition.Justification.CENTRAL ? espacementMaximal : Math.min(espacementMaximal, disposition.espacement());

        AtomicInteger longueurCourante = new AtomicInteger(0);
        if (disposition.orientation() == Disposition.Orientation.HORIZONTAL) {
            actions.forEach(action -> {
                int decalage = switch (disposition.alignement()) {
                    case DEBUT -> 0;
                    case CENTRAL -> (epaisseurActions - action.hauteur()) / 2;
                    case FIN -> epaisseurActions - action.hauteur();
                };
                action.placer(abcisses + longueurCourante.get(), ordonnee + decalage);
                longueurCourante.getAndAdd(espacementReel + action.largeur());
            });
        } else {
            actions.forEach(action -> {
                int decalage = switch (disposition.alignement()) {
                    case DEBUT -> 0;
                    case CENTRAL -> (epaisseurActions - action.largeur()) / 2;
                    case FIN -> epaisseurActions - action.largeur();
                };
                action.placer(abcisses + decalage, ordonnee + longueurCourante.get());
                longueurCourante.getAndAdd(espacementReel + action.hauteur());
            });
        }
    }

    private void placerInformations() {
        actions.forEach(action -> {
            List<Ecrit<Void>> ecrits = informations.getOrDefault(action.uuid(), Collections.emptyList());
            int largeur = ecrits.stream().mapToInt(ecrit -> ecrit.dimensionner(contexte).largeur()).max().orElse(0);
            int hauteur = ecrits.stream().mapToInt(Ecrit::hauteur).sum();
            int abscisseCourante = switch (disposition.position()) {
                case HAUT, BAS -> action.abscisse();
                case DROITE -> abcisses + epaisseurActions + MARGE_INFORMATIONS;
                case GAUCHE -> abcisses - largeur - MARGE_INFORMATIONS;
            };
            AtomicInteger ordonneeCourante = new AtomicInteger(switch (disposition.position()) {
                case HAUT -> ordonnee - hauteur - MARGE_INFORMATIONS;
                case BAS -> ordonnee + epaisseurActions + MARGE_INFORMATIONS;
                case DROITE, GAUCHE -> action.ordonnee();
            });
            abscisseEcrits.put(action.uuid(), abscisseCourante);
            ordonneeEcrits.put(action.uuid(), ordonneeCourante.get());
            largeurEcrits.put(action.uuid(), largeur);
            hauteurEcrits.put(action.uuid(), hauteur);
            ecrits.forEach(ecrit -> {
                ecrit.abscisse(abscisseCourante);
                ecrit.ordonnee(ordonneeCourante.getAndAdd(ecrit.hauteur()));
            });
        });
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

        actions.forEach(action -> {
            nvgBeginPath(contexte);
            nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
            nvgFillColor(contexte, BLANC.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            nvgBeginPath(contexte);
            nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
            nvgFillPaint(contexte, nvgImagePattern(contexte,
                    action.abscisse(),
                    action.ordonnee(),
                    action.largeur(),
                    action.hauteur(),
                    0,
                    action.image().nvg(),
                    1,
                    NVGPaint.create()));
            nvgFill(contexte);
            nvgClosePath(contexte);

            if (action.active() && action.anime()) {
                nvgBeginPath(contexte);
                nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
                nvgFillColor(contexte, INDIGO_A50.nvg());
                nvgFill(contexte);
                nvgClosePath(contexte);
            }
        });

        actionsSurvolees.forEach(action -> {
            nvgBeginPath(contexte);
            nvgRect(contexte, action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
            nvgFillColor(contexte, ROUGE_COQUELICOT_A50.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            dessinerInfobulle(action);
        });

        actionsLiees.forEach(this::dessinerInfobulle);
    }

    private void dessinerInfobulle(Action<?> action) {
        List<Ecrit<Void>> ecrits = informations.getOrDefault(action.uuid(), Collections.emptyList());
        if (!ecrits.isEmpty()) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

            nvgBeginPath(contexte);
            nvgRect(contexte,
                    abscisseEcrits.getOrDefault(action.uuid(), 0),
                    ordonneeEcrits.getOrDefault(action.uuid(), 0),
                    largeurEcrits.getOrDefault(action.uuid(), 0),
                    hauteurEcrits.getOrDefault(action.uuid(), 0));
            nvgFillColor(contexte, EMERAUDE.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            ecrits.forEach(ecrit -> {
                nvgFontSize(contexte, ecrit.taille());
                nvgFontFace(contexte, ecrit.police().identifiant());
                nvgFillColor(contexte, ecrit.couleur().nvg());
                nvgText(contexte, ecrit.abscisse(), ecrit.ordonnee(), ecrit.texte().get());
            });
        }
    }

    @Override
    public boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (visible) {
            actionsSurvolees = new LinkedList<>();
            actionsLiees = new LinkedList<>();
            for (Action<?> action : actions) {
                if (action.survoler(vision, evenementSouris)) {
                    actionsSurvolees.add(action);
                } else if (action.liaisons().stream().anyMatch(liaison -> liaison.survoler(vision, evenementSouris))) {
                    actionsLiees.add(action);
                }
            }
        } else {
            actionsSurvolees.clear();
            actionsLiees.clear();
        }
        return !actionsSurvolees.isEmpty();
    }

    @Override
    public void desurvoler() {
        actionsSurvolees.clear();
    }

    @Override
    public void amorcer(boolean droite) {
        avertirAmorcages(parent, actionsSurvolees, droite);
    }

    @Override
    public void desamorcer(boolean droite) {
        avertirAmorcages(parent, actionsSurvolees, droite);
    }

    public <T> void ajouter(G groupe, Action<T> action) {
        groupes.computeIfAbsent(groupe, _ -> new ArrayList<>()).add(action);
    }

    @SafeVarargs
    public final <T> void ajouter(G groupe, Action<T>... actions) {
        groupes.computeIfAbsent(groupe, _ -> new ArrayList<>()).addAll(List.of(actions));
    }

    public void ajouter(UUID uuid, Ecrit<Void> nouveau) {
        informations.computeIfAbsent(uuid, _ -> new LinkedList<>()).add(nouveau);
    }

    public void afficher(G groupe) {
        if (!Objects.equals(this.groupe, groupe)) {
            this.groupe = groupe;
            actions = groupes.getOrDefault(groupe, Collections.emptyList());
            placerActions();
            placerInformations();
        }
    }

    public void desactiverActions() {
        groupes.values().stream().flatMap(Collection::stream).forEach(Action::desactiver);
    }
}
