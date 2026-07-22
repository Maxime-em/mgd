package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.affichage.tetehaute.Disposition;
import org.mgd.lwjgl.affichage.tetehaute.Options;
import org.mgd.lwjgl.affichage.tetehaute.Pagination;
import org.mgd.lwjgl.affichage.tetehaute.Position;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute.BLANC;
import static org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute.EMERAUDE;

public class Liste extends Entite {
    private final Disposition disposition;
    private final Disposition dispositionSousEntites;
    private final Options options;
    private final List<Entite> persistantes;
    private final List<Entite> entites;
    private final Map<UUID, List<Entite>> sousEntitesParUuid;
    private final Map<UUID, Entite> entitesSecondairesParUuid;
    private final Pagination pagination;
    private final Entite panneau;
    private final Espacement espacement;
    private final Espacement espacementSousEntites;
    private boolean ouvert;

    public Liste(Disposition disposition, Options options, int taille) {
        super();
        this.disposition = disposition;
        this.dispositionSousEntites = new Disposition(disposition.orientation(),
                disposition.justification(),
                disposition.alignement(),
                disposition.dimensionnement(),
                disposition.marge(),
                10,
                0);
        this.options = options;
        this.persistantes = new LinkedList<>();
        this.entites = new LinkedList<>();
        this.sousEntitesParUuid = new HashMap<>();
        this.entitesSecondairesParUuid = new HashMap<>();
        this.pagination = new Pagination(taille, 0, 1);
        this.panneau = new Fond(0, 0);
        this.espacement = new Espacement();
        this.espacementSousEntites = new Espacement();
    }

    public Liste(Disposition disposition, Options options) {
        this(disposition, options, Integer.MAX_VALUE);
    }

    @Override
    public void placer(int abscisse, int ordonnee) {
        super.placer(abscisse, ordonnee);

        placerEntites(disposition, espacement, options.positionSousEntites().orElse(Position.DROITE), this, this::fluxEntitesAffichables);

        if (options.sousentites()) {
            options.positionSousEntites().ifPresent(position -> {
                placerPanneauSousEntites(abscisse, ordonnee, position);
                placerEntites(dispositionSousEntites, espacementSousEntites, position, panneau, this::fluxSousEntites);
            });
        }
    }

    private void placerPanneauSousEntites(int abscisse, int ordonnee, Position position) {
        int abscisseEntites = switch (disposition.orientation()) {
            case HORIZONTAL -> abscisse + persistantes.stream().mapToInt(Entite::largeur).sum();
            case VERTICAL -> abscisse;
        };

        int ordonneeEntites = switch (disposition.orientation()) {
            case HORIZONTAL -> ordonnee;
            case VERTICAL -> ordonnee + persistantes.stream().mapToInt(Entite::hauteur).sum();
        };

        int abscissePanneau = switch (position) {
            case HAUT, BAS -> abscisseEntites;
            case GAUCHE -> abscisseEntites - largeur();
            case DROITE -> abscisseEntites + largeur();
        };

        int ordonneePanneau = switch (position) {
            case HAUT -> abscisseEntites - hauteur();
            case BAS -> abscisseEntites + hauteur();
            case GAUCHE, DROITE -> ordonneeEntites;
        };

        panneau.placer(abscissePanneau, ordonneePanneau);
    }

    private void placerBoutonSecondaire(Entite entite, Position position) {
        if (entitesSecondairesParUuid.containsKey(entite.uuid)) {
            Entite entiteSecondaire = entitesSecondairesParUuid.get(entite.uuid);
            switch (position) {
                case HAUT -> entiteSecondaire.placer(entite.abscisse(), entite.ordonnee() - entiteSecondaire.hauteur());
                case BAS -> entiteSecondaire.placer(entite.abscisse(), entite.ordonnee() + entite.hauteur());
                case DROITE -> entiteSecondaire.placer(entite.abscisse() + entite.largeur(), entite.ordonnee());
                case GAUCHE ->
                        entiteSecondaire.placer(entite.abscisse() - entiteSecondaire.largeur(), entite.ordonnee());
            }
        }
    }

    private void placerEntites(Disposition disposition, Espacement espacement, Position position, Entite panneau, Supplier<Stream<Entite>> entites) {
        switch (disposition.orientation()) {
            case HORIZONTAL -> {
                AtomicInteger largeurCourante = new AtomicInteger(espacement.debut);
                entites.get().forEach(entite -> {
                    int decalage = switch (disposition.alignement()) {
                        case DEBUT -> 0;
                        case CENTRAL -> (hauteur() - entite.hauteur()) / 2;
                        case FIN -> hauteur() - entite.hauteur();
                    };
                    entite.placer(panneau.abscisse() + largeurCourante.getAndAdd(espacement.interne + entite.largeur()), panneau.ordonnee() + decalage);
                    placerBoutonSecondaire(entite, position);
                });
            }
            case VERTICAL -> {
                AtomicInteger hauteurCourante = new AtomicInteger(espacement.debut);
                entites.get().forEach(entite -> {
                    int decalage = switch (disposition.alignement()) {
                        case DEBUT -> 0;
                        case CENTRAL -> (panneau.largeur() - entite.largeur()) / 2;
                        case FIN -> panneau.largeur() - entite.largeur();
                    };
                    entite.placer(panneau.abscisse() + decalage, panneau.ordonnee() + hauteurCourante.getAndAdd(espacement.interne + entite.hauteur()));
                    placerBoutonSecondaire(entite, position);
                });
            }
        }
    }

    @Override
    public void dimensionner(long contexte) {
        pagination.calculer(entites.stream().filter(Entite::visible).count());

        dimensionnerPanneauEntites(contexte);

        if (options.sousentites()) {
            dimensionnerPanneauSousEntites(contexte);
        }
    }

    private void dimensionnerPanneauEntites(long contexte) {
        espacement.calculer(contexte, disposition, this::fluxEntitesAffichables);
        dimensionnerPanneau(disposition, espacement, this, this::fluxEntitesAffichables);
    }

    private void dimensionnerPanneauSousEntites(long contexte) {
        dispositionSousEntites.longueur(switch (disposition.orientation()) {
            case HORIZONTAL -> largeur() - persistantes.stream().mapToInt(Entite::largeur).sum();
            case VERTICAL -> hauteur() - persistantes.stream().mapToInt(Entite::hauteur).sum();
        });
        espacementSousEntites.calculer(contexte, dispositionSousEntites, this::fluxSousEntites);
        dimensionnerPanneau(dispositionSousEntites, espacementSousEntites, panneau, this::fluxSousEntites);
    }

    private void dimensionnerPanneau(Disposition disposition, Espacement espacement, Entite panneau, Supplier<Stream<Entite>> entites) {
        int largeur = switch (disposition.orientation()) {
            case HORIZONTAL -> switch (disposition.dimensionnement()) {
                case VARIABLE -> entites.get().mapToInt(Entite::largeur).sum() + espacement.total;
                case FIXE -> disposition.longueur();
            };
            case VERTICAL -> 2 * disposition.marge() + entites.get().mapToInt(Entite::largeur).max().orElse(0);
        };
        int hauteur = switch (disposition.orientation()) {
            case HORIZONTAL -> disposition.marge() + entites.get().mapToInt(Entite::hauteur).max().orElse(0);
            case VERTICAL -> switch (disposition.dimensionnement()) {
                case VARIABLE -> entites.get().mapToInt(Entite::hauteur).sum() + espacement.total;
                case FIXE -> disposition.longueur();
            };
        };
        panneau.proportionner(largeur, hauteur);
    }

    @Override
    public void dessiner(long contexte) {
        fluxEntitesAffichables().forEach(entite -> entite.dessiner(contexte));

        if (options.sousentites()) {
            fluxEntitesSecondaires().forEach(entite -> entite.colorier(contexte, EMERAUDE));
        }

        if (ouvert) {
            panneau.colorier(contexte, BLANC);
            fluxSousEntites().forEach(entite -> entite.dessiner(contexte));
        }
    }

    public void ajouter(Entite... entites) {
        this.entites.addAll(Arrays.asList(entites));
        options.secondaire().ifPresent(constructeur -> Arrays.stream(entites).forEach(entite -> {
            Entite entiteSecondaire = constructeur.get();
            entiteSecondaire.afficher();
            entitesSecondairesParUuid.put(entite.uuid, entiteSecondaire);
        }));
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

    public Stream<Entite> fluxEntitesSecondaires() {
        return fluxEntitesNonPersistantes().filter(entite -> entitesSecondairesParUuid.containsKey(entite.uuid)).map(entite -> entitesSecondairesParUuid.get(entite.uuid)).filter(Entite::visible);
    }

    public void ouvrirPanneauSecondaire() {
        ouvert = true;
    }

    public void fermerPanneauSecondaire() {
        ouvert = false;
    }

    public boolean panneauOuvert() {
        return ouvert;
    }

    public void hierarchiser(Entite entite, Entite sousEntite) {
        if (entites.remove(sousEntite)) {
            sousEntitesParUuid.computeIfAbsent(entite.uuid, _ -> new LinkedList<>()).add(sousEntite);
            if (entitesSecondairesParUuid.containsKey(sousEntite.uuid)) {
                entitesSecondairesParUuid.get(sousEntite.uuid).masquer();
            }
        }
    }

    public List<Entite> persistantes() {
        return persistantes;
    }

    public Pagination pagination() {
        return pagination;
    }

    public Optional<Entite> panneau() {
        return Optional.ofNullable(panneau);
    }

    private class Espacement {
        private int debut;
        private int interne;
        private int total;

        private int nombre(Stream<Entite> entites) {
            return Math.toIntExact(entites.count());
        }

        private int longueur(long contexte, Stream<Entite> entites) {
            return entites.mapToInt(entite -> {
                entite.dimensionner(contexte);
                return switch (disposition.orientation()) {
                    case HORIZONTAL -> entite.largeur();
                    case VERTICAL -> entite.hauteur();
                };
            }).sum();
        }

        public void calculer(long contexte, Disposition disposition, Supplier<Stream<Entite>> entites) {
            int nombreEntites = nombre(entites.get());
            int longueurEntites = longueur(contexte, entites.get());

            switch (disposition.dimensionnement()) {
                case VARIABLE -> {
                    interne = disposition.espacement();
                    debut = 0;
                }
                case FIXE -> {
                    int espacementMaximal = nombreEntites > 1 ? (disposition.longueur() - longueurEntites) / (nombreEntites - 1) : 0;
                    switch (disposition.justification()) {
                        case DEBUT -> {
                            interne = Math.min(espacementMaximal, disposition.espacement());
                            debut = 0;
                        }
                        case CENTRAL -> {
                            interne = Math.min(espacementMaximal, disposition.espacement());
                            debut = (disposition.longueur() - longueurEntites - (nombreEntites - 1) * interne) / 2;
                        }
                        case ETENDU -> {
                            interne = espacementMaximal;
                            debut = (disposition.longueur() - longueurEntites - (nombreEntites - 1) * interne) / 2;
                        }
                    }
                }
            }
            total = nombreEntites > 0 ? debut + (nombreEntites - 1) * interne : 0;
        }
    }
}
