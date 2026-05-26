package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Animateur;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Alignement;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Dimensionnement;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Justification;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Orientation;
import org.mgd.lwjgl.affichage.tetehaute.composant.Action;
import org.mgd.lwjgl.affichage.tetehaute.composant.ActionTextutelle;
import org.mgd.lwjgl.affichage.tetehaute.composant.Liste;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.Identifiable;

import java.util.*;

import static org.lwjgl.nanovg.NanoVG.*;

public class BarreActions<G> extends AffichageTeteHaute implements Animateur {
    private static final int MARGE_INFORMATIONS = 5;
    private static final int MARGE_TEXTES = 5;

    private final Disposition disposition;
    private final Position position;
    private final int abcisses;
    private final int ordonnee;
    private final int taille;
    private final List<Action<?>> persistantes;
    private final Map<G, Liste<Action<?>>> groupes;
    private final Map<UUID, Liste<ActionTextutelle<Void>>> informations;
    private final List<Action<?>> actionsSurvolees;
    private final List<Action<?>> actionsLiees;
    private G groupe;

    public BarreActions(Fenetre parent,
                        Disposition disposition,
                        Position position,
                        int abcisses,
                        int ordonnee,
                        int taille) throws LwjglException {
        super(parent, false, true);
        this.disposition = disposition;
        this.position = position;
        this.abcisses = abcisses;
        this.ordonnee = ordonnee;
        this.taille = taille;
        this.persistantes = new LinkedList<>();
        this.groupes = new HashMap<>();
        this.informations = new HashMap<>();
        this.actionsSurvolees = new LinkedList<>();
        this.actionsLiees = new LinkedList<>();
    }

    private Optional<Liste<Action<?>>> liste(boolean force) {
        return Optional.ofNullable(groupes.get(groupe)).map(liste -> force || liste.visible() ? liste : null);
    }

    private Optional<Liste<ActionTextutelle<Void>>> information(UUID uuid, boolean force) {
        return Optional.ofNullable(informations.get(uuid)).map(liste -> force || liste.visible() ? liste : null);
    }

    private void placer() {
        liste(false).ifPresent(liste -> {
            liste.dimensionner(contexte);
            liste.placer(abcisses, ordonnee);
            liste.fluxActionsAffichables().forEach(action -> information(action.uuid(), false).ifPresent(information -> {
                information.dimensionner(contexte);
                int epaisseur = switch (disposition.orientation()) {
                    case HORIZONTAL -> liste.hauteur();
                    case VERTICAL -> liste.largeur();
                };
                int abscisseInformation = switch (position) {
                    case HAUT, BAS -> action.abscisse();
                    case DROITE -> abcisses + epaisseur + MARGE_INFORMATIONS;
                    case GAUCHE -> abcisses - information.largeur() - MARGE_INFORMATIONS;
                };
                int ordonneeInformation = switch (position) {
                    case HAUT -> ordonnee - information.hauteur() - MARGE_INFORMATIONS;
                    case BAS -> ordonnee + epaisseur + MARGE_INFORMATIONS;
                    case DROITE, GAUCHE -> action.ordonnee();
                };

                information.placer(abscisseInformation, ordonneeInformation);
            }));
        });
    }

    @Override
    public Fenetre parent() {
        return parent;
    }

    @Override
    public void maj(long accumulateur, Vision vision, EvenementSouris evenementSouris, Fenetre.EvenementAmorcages evenementAmorcagesCourant) throws LwjglException {
        Animateur.super.maj(accumulateur, vision, evenementSouris, evenementAmorcagesCourant);
        actionsLiees.clear();
        liste(false).ifPresent(liste ->
                actionsLiees.addAll(liste
                        .fluxActionsAffichables()
                        .filter(action -> action.liaisons().stream().anyMatch(liaison -> liaison.visible() && liaison.survoler(vision, evenementSouris)))
                        .toList()));
    }

    @Override
    public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
        actionsSurvolees.clear();
        actionsLiees.clear();
        if (visible) {
            liste(false).ifPresent(liste -> {
                if (liste.suivante().survoler(vision, evenementSouris) && evenementSouris.selection()) {
                    liste.pagination().suivant();
                    placer();
                } else if (liste.precedente().survoler(vision, evenementSouris) && evenementSouris.selection()) {
                    liste.pagination().precedent();
                    placer();
                } else {
                    actionsSurvolees.addAll(liste
                            .fluxActionsAffichables()
                            .filter(actionImagee -> actionImagee.survoler(vision, evenementSouris))
                            .toList());
                }
            });
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
        nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        liste(false).ifPresent(liste -> {
            liste.colorier(contexte, AUBURN);
            liste.fluxActionsAffichables().forEach(action -> action.colorier(contexte, NOIR_A50));
            liste.dessiner(contexte);
            liste.fluxActionsAffichables()
                    .filter(action -> action.active() && action.anime())
                    .forEach(action -> action.colorier(contexte, INDIGO_A50));

            actionsSurvolees.forEach(action -> {
                action.colorier(contexte, ROUGE_COQUELICOT_A50);
                dessinerInfobulle(action);
            });

            actionsLiees.forEach(this::dessinerInfobulle);
        });
    }

    private void dessinerInfobulle(Action<?> action) {
        information(action.uuid(), false).ifPresent(liste -> {
            liste.colorier(contexte, EMERAUDE);
            liste.dessiner(contexte);
        });
    }

    public void ajouter(Action<?> action) {
        persistantes.add(action);
    }

    public void ajouter(G groupe, Action<?> action) {
        groupes.computeIfAbsent(groupe, _ -> {
            Liste<Action<?>> liste = new Liste<>(parent.contexteNvg(), disposition, taille);
            liste.persistantes().addAll(persistantes);
            return liste;
        }).actions().add(action);
    }

    @SafeVarargs
    public final <A extends Action<?>> void ajouter(G groupe, A... actions) {
        groupes.computeIfAbsent(groupe, _ -> {
            Liste<Action<?>> liste = new Liste<>(parent.contexteNvg(), disposition, taille);
            liste.persistantes().addAll(persistantes);
            return liste;
        }).actions().addAll(List.of(actions));
    }

    public void ajouter(UUID uuid, ActionTextutelle<Void> nouveau) {
        informations.computeIfAbsent(uuid, _ -> new Liste<>(parent.contexteNvg(), new Disposition(Orientation.VERTICAL, Justification.DEBUT, Alignement.DEBUT, Dimensionnement.VARIABLE, 0, MARGE_TEXTES, 0)))
                .actions()
                .add(nouveau);
    }

    public void afficher(G groupe) {
        if (!Objects.equals(this.groupe, groupe)) {
            liste(true).ifPresent(liste -> {
                liste.masquer();
                liste.actions().forEach(action -> {
                    action.masquer();
                    information(action.uuid(), true).ifPresent(information -> {
                        information.masquer();
                        information.actions().forEach(Action::masquer);
                    });
                });
                liste.persistantes().forEach(action -> {
                    action.masquer();
                    information(action.uuid(), true).ifPresent(information -> {
                        information.masquer();
                        information.actions().forEach(Action::masquer);
                    });
                });
            });
            this.groupe = groupe;
            liste(true).ifPresent(liste -> {
                liste.afficher();
                liste.actions().forEach(action -> {
                    action.afficher();
                    information(action.uuid(), true).ifPresent(information -> {
                        information.afficher();
                        information.actions().forEach(Action::afficher);
                    });
                });
                liste.persistantes().forEach(action -> {
                    action.afficher();
                    information(action.uuid(), true).ifPresent(information -> {
                        information.afficher();
                        information.actions().forEach(Action::afficher);
                    });
                });
            });
            placer();
        }
    }

    public void desactiverActions() {
        groupes.values().stream().flatMap(liste -> liste.actions().stream()).forEach(Action::desactiver);
    }
}
