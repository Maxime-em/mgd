package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.affichage.tetehaute.Disposition;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Dimensionnement;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Liste<A extends Action<?>> extends Entite {
    private final Disposition disposition;
    private final List<A> actions;
    private int espacementDebut;
    private int espacementInterne;

    public Liste(Disposition disposition) {
        this.disposition = disposition;
        this.actions = new LinkedList<>();
    }

    @Override
    public void placer(int abscisse, int ordonnee) {
        super.placer(abscisse, ordonnee);

        if (disposition.orientation() == Disposition.Orientation.HORIZONTAL) {
            AtomicInteger largeurCourante = new AtomicInteger(espacementDebut);
            actions.forEach(action -> {
                int decalage = switch (disposition.alignement()) {
                    case DEBUT -> 0;
                    case CENTRAL -> (hauteur - action.hauteur) / 2;
                    case FIN -> hauteur - action.hauteur;
                };
                action.placer(abscisse + largeurCourante.getAndAdd(espacementInterne + action.largeur()), ordonnee + disposition.marge() + decalage);
            });
        } else if (disposition.orientation() == Disposition.Orientation.VERTICAL) {
            AtomicInteger hauteurCourante = new AtomicInteger(espacementDebut);
            actions.forEach(action -> {
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
        int longueurActions = actions.stream().mapToInt(action -> {
            action.dimensionner(contexte);
            return switch (disposition.orientation()) {
                case HORIZONTAL -> action.largeur;
                case VERTICAL -> action.hauteur;
            };
        }).sum();

        if (disposition.dimensionnement() == Dimensionnement.VARIABLE) {
            espacementInterne = disposition.espacement();
            espacementDebut = 0;
        } else if (disposition.dimensionnement() == Dimensionnement.FIXE) {
            int espacementMaximal = actions.size() > 1 ? (disposition.longueur() - longueurActions) / (actions.size() - 1) : 0;
            switch (disposition.justification()) {
                case DEBUT -> {
                    espacementInterne = Math.min(espacementMaximal, disposition.espacement());
                    espacementDebut = 0;
                }
                case CENTRAL -> {
                    espacementInterne = Math.min(espacementMaximal, disposition.espacement());
                    espacementDebut = (disposition.longueur() - longueurActions - (actions.size() - 1) * espacementInterne) / 2;
                }
                case ETENDU -> {
                    espacementInterne = espacementMaximal;
                    espacementDebut = (disposition.longueur() - longueurActions - (actions.size() - 1) * espacementInterne) / 2;
                }
            }
        }

        if (disposition.orientation() == Disposition.Orientation.HORIZONTAL) {
            hauteur = disposition.marge() + actions.stream().mapToInt(action -> action.hauteur).max().orElse(0);
            largeur = switch (disposition.dimensionnement()) {
                case FIXE -> disposition.longueur();
                case VARIABLE -> actions.stream().mapToInt(action -> action.largeur).sum()
                        + espacementDebut
                        + (actions.size() - 1) * espacementInterne;
            };
        } else if (disposition.orientation() == Disposition.Orientation.VERTICAL) {
            hauteur = switch (disposition.dimensionnement()) {
                case FIXE -> disposition.longueur();
                case VARIABLE -> actions.stream().mapToInt(action -> action.hauteur).sum()
                        + espacementDebut
                        + (actions.size() - 1) * espacementInterne;
            };
            largeur = disposition.marge() + actions.stream().mapToInt(action -> action.largeur).max().orElse(0);
        }
    }

    @Override
    public void dessiner(long contexte) {
        actions.forEach(action -> action.dessiner(contexte));
    }

    public List<A> actions() {
        return actions;
    }
}
