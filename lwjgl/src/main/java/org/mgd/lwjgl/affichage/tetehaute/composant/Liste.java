package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute;
import org.mgd.lwjgl.affichage.tetehaute.Disposition;
import org.mgd.lwjgl.affichage.tetehaute.Pagination;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute.BLANC;

public class Liste<A extends Action<?>> extends Entite {
    private final Disposition disposition;
    private final List<A> persistantes;
    private final List<A> actions;
    private final Pagination pagination;
    private final Action<Void> suivante;
    private final Action<Void> precedente;
    private int espacementDebut;
    private int espacementInterne;

    public Liste(long contexte, Disposition disposition, int taille) {
        this.disposition = disposition;
        this.persistantes = new LinkedList<>();
        this.actions = new LinkedList<>();
        this.pagination = new Pagination(taille, 0, 1);
        this.suivante = new ActionTextutelle<>(24f, AffichageTeteHaute.obtenirPolice(contexte, "Calibri"), BLANC, () -> "Suivant");
        this.precedente = new ActionTextutelle<>(24f, AffichageTeteHaute.obtenirPolice(contexte, "Calibri"), BLANC, () -> "Précedent");
    }

    public Liste(long contexte, Disposition disposition) {
        this(contexte, disposition, Integer.MAX_VALUE);
    }

    @Override
    public void placer(int abscisse, int ordonnee) {
        super.placer(abscisse, ordonnee);

        if (disposition.orientation() == Disposition.Orientation.HORIZONTAL) {
            AtomicInteger largeurCourante = new AtomicInteger(espacementDebut);
            fluxActionsAffichables().forEach(action -> {
                int decalage = switch (disposition.alignement()) {
                    case DEBUT -> 0;
                    case CENTRAL -> (hauteur - action.hauteur) / 2;
                    case FIN -> hauteur - action.hauteur;
                };
                action.placer(abscisse + largeurCourante.getAndAdd(espacementInterne + action.largeur()), ordonnee + disposition.marge() + decalage);
            });
        } else if (disposition.orientation() == Disposition.Orientation.VERTICAL) {
            AtomicInteger hauteurCourante = new AtomicInteger(espacementDebut);
            fluxActionsAffichables().forEach(action -> {
                int decalage = switch (disposition.alignement()) {
                    case DEBUT -> 0;
                    case CENTRAL -> (largeur - action.largeur) / 2;
                    case FIN -> largeur - action.largeur;
                };
                action.placer(abscisse + disposition.marge() + decalage, ordonnee + hauteurCourante.getAndAdd(espacementInterne + action.hauteur));
            });
        }
    }

    @Override
    public void dimensionner(long contexte) {
        int longueurActions = fluxActionsAffichables().mapToInt(action -> {
            action.dimensionner(contexte);
            return switch (disposition.orientation()) {
                case HORIZONTAL -> action.largeur;
                case VERTICAL -> action.hauteur;
            };
        }).sum();

        pagination.calculer(actions.stream().filter(Entite::visible).count());
        int nombreActions = Math.toIntExact(fluxActionsAffichables().count());

        switch (disposition.dimensionnement()) {
            case VARIABLE -> {
                espacementInterne = disposition.espacement();
                espacementDebut = 0;
                switch (disposition.orientation()) {
                    case HORIZONTAL -> {
                        hauteur = disposition.marge() + fluxActionsAffichables().mapToInt(action -> action.hauteur).max().orElse(0);
                        largeur = fluxActionsAffichables().mapToInt(action -> action.largeur).sum()
                                + espacementDebut
                                + (nombreActions - 1) * espacementInterne;
                    }
                    case VERTICAL -> {
                        hauteur = fluxActionsAffichables().mapToInt(action -> action.hauteur).sum()
                                + espacementDebut
                                + (nombreActions - 1) * espacementInterne;
                        largeur = disposition.marge() + fluxActionsAffichables().mapToInt(action -> action.largeur).max().orElse(0);
                    }
                }
            }
            case FIXE -> {
                int espacementMaximal = nombreActions > 1 ? (disposition.longueur() - longueurActions) / (nombreActions - 1) : 0;
                switch (disposition.justification()) {
                    case DEBUT -> {
                        espacementInterne = Math.min(espacementMaximal, disposition.espacement());
                        espacementDebut = 0;
                    }
                    case CENTRAL -> {
                        espacementInterne = Math.min(espacementMaximal, disposition.espacement());
                        espacementDebut = (disposition.longueur() - longueurActions - (nombreActions - 1) * espacementInterne) / 2;
                    }
                    case ETENDU -> {
                        espacementInterne = espacementMaximal;
                        espacementDebut = (disposition.longueur() - longueurActions - (nombreActions - 1) * espacementInterne) / 2;
                    }
                }
                switch (disposition.orientation()) {
                    case HORIZONTAL -> {
                        hauteur = disposition.marge() + fluxActionsAffichables().mapToInt(action -> action.hauteur).max().orElse(0);
                        largeur = disposition.longueur();
                    }
                    case VERTICAL -> {
                        hauteur = disposition.longueur();
                        largeur = disposition.marge() + fluxActionsAffichables().mapToInt(action -> action.largeur).max().orElse(0);
                    }
                }
            }
        }

        this.suivante.dimensionner(contexte);
        this.precedente.dessiner(contexte);
    }

    @Override
    public void dessiner(long contexte) {
        fluxActionsAffichables().forEach(action -> action.dessiner(contexte));
    }

    public Stream<Action<?>> fluxActionsAffichables() {
        Stream<Action<?>> fluxPagination = Stream.concat(
                pagination.page() > 0 ? Stream.of(precedente) : Stream.empty(),
                pagination.page() < pagination.total() - 1 ? Stream.of(suivante) : Stream.empty());
        return Stream.concat(Stream.concat(persistantes.stream(), fluxPagination), actions.stream().filter(Entite::visible).skip((long) pagination.page() * pagination.taille()).limit(pagination.taille()));
    }

    public List<A> persistantes() {
        return persistantes;
    }

    public List<A> actions() {
        return actions;
    }

    public Pagination pagination() {
        return pagination;
    }

    public Action<Void> suivante() {
        return suivante;
    }

    public Action<Void> precedente() {
        return precedente;
    }
}
