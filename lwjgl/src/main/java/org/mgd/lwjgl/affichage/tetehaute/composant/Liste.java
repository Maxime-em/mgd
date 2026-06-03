package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute;
import org.mgd.lwjgl.affichage.tetehaute.Disposition;
import org.mgd.lwjgl.affichage.tetehaute.Pagination;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute.BLANC;

public class Liste extends Entite {
    private final Disposition disposition;
    private final List<Entite> persistantes;
    private final List<Entite> entites;
    private final Pagination pagination;
    private final Entite suivante;
    private final Entite precedente;
    private int espacementDebut;
    private int espacementInterne;

    public Liste(long contexte, Disposition disposition, int taille) {
        super();
        this.disposition = disposition;
        this.persistantes = new LinkedList<>();
        this.entites = new LinkedList<>();
        this.pagination = new Pagination(taille, 0, 1);
        this.suivante = new ActionTextutelle<Void>(24f, AffichageTeteHaute.obtenirPolice(contexte, "Calibri"), BLANC, () -> "Suivant");
        this.precedente = new ActionTextutelle<Void>(24f, AffichageTeteHaute.obtenirPolice(contexte, "Calibri"), BLANC, () -> "Précedent");
    }

    public Liste(long contexte, Disposition disposition) {
        this(contexte, disposition, Integer.MAX_VALUE);
    }

    @Override
    public void placer(int abscisse, int ordonnee) {
        super.placer(abscisse, ordonnee);

        if (disposition.orientation() == Disposition.Orientation.HORIZONTAL) {
            AtomicInteger largeurCourante = new AtomicInteger(espacementDebut);
            fluxEntitesAffichables().forEach(entite -> {
                int decalage = switch (disposition.alignement()) {
                    case DEBUT -> 0;
                    case CENTRAL -> (hauteur() - entite.hauteur()) / 2;
                    case FIN -> hauteur() - entite.hauteur();
                };
                entite.placer(abscisse + largeurCourante.getAndAdd(espacementInterne + entite.largeur()), ordonnee + disposition.marge() + decalage);
            });
        } else if (disposition.orientation() == Disposition.Orientation.VERTICAL) {
            AtomicInteger hauteurCourante = new AtomicInteger(espacementDebut);
            fluxEntitesAffichables().forEach(entite -> {
                int decalage = switch (disposition.alignement()) {
                    case DEBUT -> 0;
                    case CENTRAL -> (largeur() - entite.largeur()) / 2;
                    case FIN -> largeur() - entite.largeur();
                };
                entite.placer(abscisse + disposition.marge() + decalage, ordonnee + hauteurCourante.getAndAdd(espacementInterne + entite.hauteur()));
            });
        }
    }

    @Override
    public void dimensionner(long contexte) {
        pagination.calculer(entites.stream().filter(Entite::visible).count());
        int nombreEntites = Math.toIntExact(fluxEntitesAffichables().count());
        int longueurEntites = fluxEntitesAffichables().mapToInt(entite -> {
            entite.dimensionner(contexte);
            return switch (disposition.orientation()) {
                case HORIZONTAL -> entite.largeur();
                case VERTICAL -> entite.hauteur();
            };
        }).sum();

        switch (disposition.dimensionnement()) {
            case VARIABLE -> {
                espacementInterne = disposition.espacement();
                espacementDebut = 0;
                switch (disposition.orientation()) {
                    case HORIZONTAL ->
                            proportionner(fluxEntitesAffichables().mapToInt(Entite::largeur).sum() + espacementDebut + (nombreEntites - 1) * espacementInterne,
                                    disposition.marge() + fluxEntitesAffichables().mapToInt(Entite::hauteur).max().orElse(0));
                    case VERTICAL ->
                            proportionner(disposition.marge() + fluxEntitesAffichables().mapToInt(Entite::largeur).max().orElse(0),
                                    fluxEntitesAffichables().mapToInt(Entite::hauteur).sum() + espacementDebut + (nombreEntites - 1) * espacementInterne);
                }
            }
            case FIXE -> {
                int espacementMaximal = nombreEntites > 1 ? (disposition.longueur() - longueurEntites) / (nombreEntites - 1) : 0;
                switch (disposition.justification()) {
                    case DEBUT -> {
                        espacementInterne = Math.min(espacementMaximal, disposition.espacement());
                        espacementDebut = 0;
                    }
                    case CENTRAL -> {
                        espacementInterne = Math.min(espacementMaximal, disposition.espacement());
                        espacementDebut = (disposition.longueur() - longueurEntites - (nombreEntites - 1) * espacementInterne) / 2;
                    }
                    case ETENDU -> {
                        espacementInterne = espacementMaximal;
                        espacementDebut = (disposition.longueur() - longueurEntites - (nombreEntites - 1) * espacementInterne) / 2;
                    }
                }
                switch (disposition.orientation()) {
                    case HORIZONTAL ->
                            proportionner(disposition.longueur(), disposition.marge() + fluxEntitesAffichables().mapToInt(Entite::hauteur).max().orElse(0));
                    case VERTICAL ->
                            proportionner(disposition.marge() + fluxEntitesAffichables().mapToInt(Entite::largeur).max().orElse(0), disposition.longueur());
                }
            }
        }

        this.suivante.dimensionner(contexte);
        this.precedente.dessiner(contexte);
    }

    @Override
    public void dessiner(long contexte) {
        fluxEntitesAffichables().forEach(entite -> entite.dessiner(contexte));
    }

    public Stream<Entite> fluxEntitesAffichables() {
        if (pagination().total() == 1) {
            return Stream.of(persistantes.stream(), entites.stream().filter(Entite::visible)).flatMap(Function.identity());
        } else if (pagination.page() == 0) {
            return Stream.of(persistantes.stream(), Stream.of(suivante), entites.stream().filter(Entite::visible).limit(pagination.taille() + 1L))
                    .flatMap(Function.identity());
        } else if (pagination().page() == pagination.total() - 1) {
            return Stream.of(persistantes.stream(),
                            entites.stream().filter(Entite::visible).skip((long) pagination.page() * pagination.taille() + 1L).limit(pagination.taille() + 1L),
                            Stream.of(precedente))
                    .flatMap(Function.identity());
        } else {
            return Stream.of(persistantes.stream(),
                            Stream.of(suivante),
                            entites.stream().filter(Entite::visible).skip((long) pagination.page() * pagination.taille() + 1L).limit(pagination.taille()),
                            Stream.of(precedente))
                    .flatMap(Function.identity());
        }
    }

    public List<Entite> persistantes() {
        return persistantes;
    }

    public List<Entite> entites() {
        return entites;
    }

    public Pagination pagination() {
        return pagination;
    }

    public Entite suivante() {
        return suivante;
    }

    public Entite precedente() {
        return precedente;
    }
}
