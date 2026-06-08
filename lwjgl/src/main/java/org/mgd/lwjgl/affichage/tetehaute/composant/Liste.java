package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.affichage.tetehaute.Disposition;
import org.mgd.lwjgl.affichage.tetehaute.Options;
import org.mgd.lwjgl.affichage.tetehaute.Pagination;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute.BLANC;

public class Liste extends Entite {
    private final Disposition disposition;
    private final Options options;
    private final List<Entite> persistantes;
    private final List<Entite> entites;
    private final Map<UUID, List<Entite>> sousEntitesParUuid;
    private final Pagination pagination;
    private final Entite panneau;
    private int espacementDebut;
    private int espacementInterne;
    private boolean ouvert;

    public Liste(Disposition disposition, Options options, int taille) {
        super();
        this.disposition = disposition;
        this.options = options;
        this.persistantes = new LinkedList<>();
        this.entites = new LinkedList<>();
        this.sousEntitesParUuid = new HashMap<>();
        this.pagination = new Pagination(taille, 0, 1);
        this.panneau = new Fond(0, 0);
    }

    public Liste(Disposition disposition, Options options) {
        this(disposition, options, Integer.MAX_VALUE);
    }

    @Override
    public void placer(int abscisse, int ordonnee) {
        super.placer(abscisse, ordonnee);

        placerEntites(abscisse, ordonnee);

        if (options.sousentites()) {
            placerSousEntites(abscisse, ordonnee);
        }
    }

    private void placerEntites(int abscisse, int ordonnee) {
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

    private void placerSousEntites(int abscisse, int ordonnee) {
        int abscissePanneau = switch (disposition.orientation()) {
            case HORIZONTAL -> abscisse + persistantes.stream().mapToInt(Entite::largeur).sum();
            case VERTICAL -> abscisse;
        };

        int ordonneePanneau = switch (disposition.orientation()) {
            case HORIZONTAL -> ordonnee;
            case VERTICAL -> ordonnee + persistantes.stream().mapToInt(Entite::hauteur).sum();
        };

        options.positionSousEntites().ifPresent(position -> {
            switch (position) {
                case HAUT -> {
                    options.secondaire().ifPresent(secondaire -> secondaire.placer(abscisse + largeur() - secondaire.largeur(), ordonnee));
                    panneau.placer(abscissePanneau, ordonneePanneau - hauteur());
                }
                case BAS -> {
                    options.secondaire().ifPresent(secondaire -> secondaire.placer(abscisse + largeur() - secondaire.largeur(), ordonnee + hauteur() - secondaire.hauteur()));
                    panneau.placer(abscissePanneau, ordonneePanneau + hauteur());
                }
                case GAUCHE -> {
                    options.secondaire().ifPresent(secondaire -> secondaire.placer(abscisse, ordonnee + hauteur() - secondaire.hauteur()));
                    panneau.placer(abscissePanneau - largeur(), ordonneePanneau);
                }
                case DROITE -> {
                    options.secondaire().ifPresent(secondaire -> secondaire.placer(abscisse + largeur() - secondaire.largeur(), ordonnee + hauteur() - secondaire.hauteur()));
                    panneau.placer(abscissePanneau + largeur(), ordonneePanneau);
                }
            }
        });
    }

    @Override
    public void dimensionner(long contexte) {
        pagination.calculer(entites.stream().filter(Entite::visible).count());

        dimensionnerEntites(contexte);

        if (options.sousentites()) {
            dimensionnerSousEntites(contexte);
        }
    }

    private void dimensionnerEntites(long contexte) {
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
    }

    private void dimensionnerSousEntites(long contexte) {
        options.secondaire().ifPresent(secondaire -> secondaire.dimensionner(contexte));

        switch (disposition.orientation()) {
            case HORIZONTAL ->
                    panneau.proportionner(largeur() - persistantes.stream().mapToInt(Entite::largeur).sum(), hauteur());
            case VERTICAL ->
                    panneau.proportionner(largeur(), hauteur() - persistantes.stream().mapToInt(Entite::hauteur).sum());
        }

        fluxSousEntites().forEach(entite -> entite.dimensionner(contexte));
    }

    @Override
    public void dessiner(long contexte) {
        fluxEntitesAffichables().forEach(entite -> entite.dessiner(contexte));

        if (options.sousentites()) {
            options.secondaire().ifPresent(secondaire -> secondaire.dessiner(contexte));
        }

        if (ouvert) {
            panneau.colorier(contexte, BLANC);
            fluxSousEntites().forEach(entite -> entite.dessiner(contexte));
        }
    }

    public void ajouter(Entite... entites) {
        this.entites.addAll(Arrays.asList(entites));
    }

    public Stream<Entite> fluxEntites() {
        return entites.stream();
    }

    public Stream<Entite> fluxEntitesAffichables() {
        if (pagination().total() == 1) {
            return Stream.of(persistantes.stream(), fluxEntitesNonPersistantes()).flatMap(Function.identity());
        } else if (pagination.page() == 0) {
            return Stream.of(persistantes.stream(), options.suivante().stream(), fluxEntitesNonPersistantes()).flatMap(Function.identity());
        } else if (pagination().page() == pagination.total() - 1) {
            return Stream.of(persistantes.stream(), fluxEntitesNonPersistantes(), options.precedente().stream()).flatMap(Function.identity());
        } else {
            return Stream.of(persistantes.stream(), options.suivante().stream(), fluxEntitesNonPersistantes(), options.precedente().stream()).flatMap(Function.identity());
        }
    }

    private Stream<Entite> fluxEntitesNonPersistantes() {
        return entites.stream().filter(Entite::visible).skip((long) pagination.page() * pagination.taille()).limit(pagination.taille());
    }

    private Stream<Entite> fluxSousEntites() {
        return entites.stream()
                .filter(Entite::active)
                .findFirst()
                .map(entite -> sousEntitesParUuid.getOrDefault(entite.uuid, Collections.emptyList()).stream())
                .orElseGet(Stream::empty);
    }

    public void panneauSecondaire() {
        ouvert = !ouvert;
    }

    public void hierarchiser(Entite entite, Entite sousEntite) {
        if (entites.remove(sousEntite)) {
            sousEntitesParUuid.computeIfAbsent(entite.uuid, _ -> new LinkedList<>()).add(sousEntite);
        }
    }

    public List<Entite> persistantes() {
        return persistantes;
    }

    public Pagination pagination() {
        return pagination;
    }
}
