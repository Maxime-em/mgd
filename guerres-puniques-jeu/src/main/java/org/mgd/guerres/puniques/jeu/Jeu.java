package org.mgd.guerres.puniques.jeu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.guerres.puniques.coeur.Jabm;
import org.mgd.guerres.puniques.coeur.JabmConnexion;
import org.mgd.guerres.puniques.coeur.objet.*;
import org.mgd.guerres.puniques.jeu.exception.JeuException;
import org.mgd.guerres.puniques.jeu.souscription.*;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.utilitaire.Fichiers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Jeu {
    public static final String NOM_GROUPE_TYPES_UNITES = "unites";
    public static final String NOM_GROUPE_TYPES_TRANSPORTS = "transports";
    public static final String NOM_GROUPE_TYPES_ARMEES = "armees";

    private static final Logger LOGGER = LogManager.getLogger(Jeu.class);
    private final Properties proprietes;
    private final Jabm jabm;
    private final Registre registre;
    private final LinkedList<ChangementPartie> changementsParties;
    private final LinkedList<ChangementSelection> changementsSelection;
    private final LinkedList<ChangementDeselection> changementsDeselection;
    private final LinkedList<ChangementSelectionCivilisation> changementsSelectionCivilisation;
    private final LinkedList<ChangementDesCivilisation> changementsDesCivilisation;
    private final LinkedList<ChangementDesActions> changementsDesActions;
    private final LinkedList<ChangementDeploiement> changementsDeploiement;
    private final LinkedList<ChangementDeplacement> changementsDeplacement;
    private final LinkedList<ChangementAttaque> changementsAttaque;
    private final LinkedList<ChangementAttaqueCivilisation> changementsAttaqueCivilisation;
    private final LinkedList<ChangementFinTour> changementsFinTour;
    private final String[] aliass;

    private Partie partieEnCours;
    private Tangible<? extends Type> selection;

    public Jeu(Properties proprietes) throws JeuException {
        try {
            this.proprietes = proprietes;
            this.jabm = new JabmConnexion(proprietes).ouvrir().getInstance();
            this.registre = this.jabm.registre();
            this.changementsParties = new LinkedList<>();
            this.changementsSelection = new LinkedList<>();
            this.changementsDeselection = new LinkedList<>();
            this.changementsSelectionCivilisation = new LinkedList<>();
            this.changementsDesCivilisation = new LinkedList<>();
            this.changementsDesActions = new LinkedList<>();
            this.changementsDeploiement = new LinkedList<>();
            this.changementsDeplacement = new LinkedList<>();
            this.changementsAttaque = new LinkedList<>();
            this.changementsAttaqueCivilisation = new LinkedList<>();
            this.changementsFinTour = new LinkedList<>();
            this.aliass = obtenirCivilisations();
        } catch (ConnexionException | IOException | JaoExecutionException | JaoParseException e) {
            throw new JeuException(e);
        }
    }

    public Registre registre() throws JeuException {
        try {
            return jabm.registre();
        } catch (IOException | JaoExecutionException | JaoParseException e) {
            throw new JeuException(e);
        }
    }

    public void nouvellePartie(int[] taille) {
        try {
            int rang = 1;
            AtomicReference<String> nouveauNom = new AtomicReference<>("Partie " + rang);
            while (registre.getInformations().values().stream().anyMatch(informations -> informations.getNom().equals(nouveauNom.get()))) {
                nouveauNom.set("Partie " + ++rang);
            }
            UUID uuidFichier = UUID.randomUUID();
            Informations informations = jabm.creerInformations(nouveauNom.get());
            registre.getInformations().put(uuidFichier, informations);
            registre.ajouterEnfant(informations);
            registre.sauvegarder();

            partieEnCours = jabm.creerPartie(informations, taille);
            partieEnCours.setInformations(informations);

            List<TypeRegion> typesRegion = Arrays.stream(obtenirTypesRegions())
                    .map(this::nouveauTypeRegion)
                    .filter(Optional::isPresent)
                    .map(option -> {
                        TypeRegion type = option.get();
                        type.ajouterParent(partieEnCours);
                        return type;
                    })
                    .toList();
            partieEnCours.getMonde().getTypes().addAll(typesRegion);

            Map<String, Civilisation> civilisations = new HashMap<>();
            Arrays.stream(aliass).forEach(alias -> {
                try {
                    List<TypeUnite> typesUnites = fluxTypes(alias, NOM_GROUPE_TYPES_UNITES)
                            .map(type -> nouveauTypeUnite(alias, type, typesRegion))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();

                    List<TypeTransport> typesTransports = fluxTypes(alias, NOM_GROUPE_TYPES_TRANSPORTS)
                            .map(type -> nouveauTypeTransport(alias, type, typesRegion))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();

                    List<TypeArmee> typeArmees = fluxTypes(alias, NOM_GROUPE_TYPES_ARMEES)
                            .map(type -> nouveauTypeArmee(alias, type))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();

                    Civilisation civilisation = jabm.creerCivilisation(obtenirProprieteCivilisation(alias, "nom"),
                            typesUnites,
                            typesTransports,
                            typeArmees,
                            partieEnCours.getMonde().getRegion(obtenirIndexCapitaleCivilisation(alias)));

                    List<Unite> unites = typesUnites.stream()
                            .flatMap(type -> IntStream.range(0, type.getMaximum())
                                    .mapToObj(_ -> nouvelleUnite(civilisation, type))
                                    .filter(Optional::isPresent)
                                    .map(Optional::get))
                            .toList();
                    civilisation.getReserve().getUnites().addAll(unites);

                    partieEnCours.getCivilisations().add(civilisation);
                    partieEnCours.ajouterEnfant(civilisation);
                    civilisations.put(obtenirProprieteCivilisation(alias, "code"), civilisation);
                } catch (JaoExecutionException | JaoParseException | NumberFormatException e) {
                    LOGGER.error("Impossible de construire la civilisation {}.", aliass, e);
                }
            });

            parcourirRegionCarte("types", (region, codes) -> region.getTypes().addAll(partieEnCours.getMonde().getTypes(codes)));
            parcourirRegionCarte("alignements", (Region region, String codes) -> {
                try {
                    region.ajouterAlignementAmi(codes, civilisations);
                    region.ajouterParent(partieEnCours);
                } catch (JaoExecutionException | JaoParseException e) {
                    LOGGER.error("Impossible d'ajouter les alignements", e);
                }
            });

            jabm.persister(uuidFichier.toString(), partieEnCours);

            changementsParties.forEach(changement -> changement.traiter(partieEnCours));
            selection = null;
        } catch (IOException | JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de créer une nouvelle partie.", e);
        }
    }

    private Optional<TypeRegion> nouveauTypeRegion(String nom) {
        try {
            return Optional.of(jabm.creerTypeRegion(obtenirCodeTypeRegion(nom)));
        } catch (JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de construire le type de région {}.", nom, e);
            return Optional.empty();
        }
    }

    private Optional<TypeArmee> nouveauTypeArmee(String alias, String type) {
        try {
            TypeArmee typeTransport = jabm.creerTypeArmee(type,
                    obtenirTextureType(alias, NOM_GROUPE_TYPES_ARMEES, type),
                    obtenirLibelleType(alias, NOM_GROUPE_TYPES_ARMEES, type),
                    obtenirMaximumType(alias, NOM_GROUPE_TYPES_ARMEES, type));
            partieEnCours.ajouterEnfant(typeTransport);
            return Optional.of(typeTransport);
        } catch (JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de construire le type d'armée {} de la civilisation {}.", type, alias, e);
            return Optional.empty();
        }
    }

    private Optional<TypeTransport> nouveauTypeTransport(String alias, String type, Collection<TypeRegion> typeRegions) {
        try {
            TypeTransport typeTransport = jabm.creerTypeTransport(type,
                    obtenirPraticablesType(alias, NOM_GROUPE_TYPES_TRANSPORTS, type, typeRegions),
                    obtenirTextureType(alias, NOM_GROUPE_TYPES_TRANSPORTS, type),
                    obtenirLibelleType(alias, NOM_GROUPE_TYPES_TRANSPORTS, type),
                    obtenirMaximumType(alias, NOM_GROUPE_TYPES_TRANSPORTS, type));
            partieEnCours.ajouterEnfant(typeTransport);
            return Optional.of(typeTransport);
        } catch (JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de construire le type de transport {} de la civilisation {}.", type, alias, e);
            return Optional.empty();
        }
    }

    private Optional<TypeUnite> nouveauTypeUnite(String alias, String type, Collection<TypeRegion> typeRegions) {
        try {
            TypeUnite typeUnite = jabm.creerTypeUnite(type,
                    obtenirPraticablesType(alias, NOM_GROUPE_TYPES_UNITES, type, typeRegions),
                    obtenirLibelleType(alias, NOM_GROUPE_TYPES_UNITES, type),
                    obtenirMaximumType(alias, NOM_GROUPE_TYPES_UNITES, type),
                    obtenirNombreType(alias, NOM_GROUPE_TYPES_UNITES, type, "vie"),
                    obtenirNombreType(alias, NOM_GROUPE_TYPES_UNITES, type, "force"));
            partieEnCours.ajouterEnfant(typeUnite);
            return Optional.of(typeUnite);
        } catch (JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de construire le type d'unité {} de la civilisation {}.", type, alias, e);
            return Optional.empty();
        }
    }

    private Optional<Unite> nouvelleUnite(Civilisation civilisation, TypeUnite type) {
        try {
            Unite unite = jabm.creerUnite(civilisation, type);
            partieEnCours.ajouterEnfant(unite);
            return Optional.of(unite);
        } catch (JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de construire une unité de type {}.", type, e);
            return Optional.empty();
        }
    }

    private void parcourirRegionCarte(String nom, BiConsumer<Region, String> action) {
        String fichier = obtenirCarte(nom);
        if (fichier == null) {
            throw new IllegalStateException(MessageFormat.format("Aucun fichier pour la carte {0} n''existe pas dans le fichier de configuration.", nom));
        }
        Path chemin = Path.of(fichier);
        if (!Files.isRegularFile(chemin)) {
            throw new IllegalStateException(MessageFormat.format("Le chemin {0} doit être un fichier.", chemin));
        }

        Fichiers.parcourir(chemin, (ligne, colonne) -> partieEnCours.getMonde().getRegion(ligne, colonne), action);
    }

    private String obtenirCarte(String nom) {
        return proprietes.getProperty(MessageFormat.format("application.jeu.regions.{0}.carte", nom));
    }

    private String[] obtenirCivilisations() {
        return proprietes.getProperty("application.jeu.civilisations").split(";");
    }

    private String obtenirProprieteCivilisation(String alias, String nom) {
        return proprietes.getProperty(MessageFormat.format("application.jeu.civilisations.{0}.{1}", alias, nom));
    }

    private Integer[] obtenirIndexCapitaleCivilisation(String alias) {
        return Arrays.stream((obtenirProprieteCivilisation(alias, "capitale").split(";"))).map(Integer::valueOf).toArray(Integer[]::new);
    }

    private String[] obtenirTypesRegions() {
        return proprietes.getProperty("application.jeu.regions.types").split(";");
    }

    private String obtenirCodeTypeRegion(String type) {
        return proprietes.getProperty(MessageFormat.format("application.jeu.regions.{0}.{1}", type, "code"));
    }

    private Stream<String> fluxTypes(String alias, String groupe) {
        return Arrays.stream(proprietes.getProperty(MessageFormat.format("application.jeu.civilisations.{0}.{1}.types", alias, groupe)).split(";"));
    }

    private String obtenirProprieteType(String alias, String groupe, String type, String nom) {
        return proprietes.getProperty(MessageFormat.format("application.jeu.civilisations.{0}.{1}.{2}.{3}", alias, groupe, type, nom));
    }

    private String obtenirLibelleType(String alias, String groupe, String type) {
        return obtenirProprieteType(alias, groupe, type, "libelle");
    }

    private List<TypeRegion> obtenirPraticablesType(String alias, String groupe, String type, Collection<TypeRegion> typeRegions) {
        return Arrays.stream(obtenirProprieteType(alias, groupe, type, "praticables").split(";"))
                .map(praticable -> typeRegions.stream().filter(typeRegion -> Objects.equals(praticable, typeRegion.getCode())).findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Integer[] obtenirTextureType(String alias, String groupe, String type) {
        return Arrays.stream(obtenirProprieteType(alias, groupe, type, "texture").split(";")).map(Integer::valueOf).toArray(Integer[]::new);
    }

    private Integer obtenirNombreType(String alias, String groupe, String type, String nom) {
        return Integer.valueOf(obtenirProprieteType(alias, groupe, type, nom));
    }

    private Integer obtenirMaximumType(String alias, String groupe, String type) {
        return obtenirNombreType(alias, groupe, type, "maximum");
    }

    public void demarrerPartie(UUID uuidFichier) {
        try {
            partieEnCours = jabm.partie(uuidFichier);
            changementsParties.forEach(changement -> changement.traiter(partieEnCours));
        } catch (IOException | JaoExecutionException | JaoParseException e) {
            LOGGER.error(MessageFormat.format("Impossible de démarrer la partie {0}.", uuidFichier), e);
        }
    }

    public void lancerDes() {
        partieEnCours.getDesCivilisation().lancer();
        changementsDesCivilisation.forEach(changement -> changement.traiter(partieEnCours.getDesCivilisation()));
        partieEnCours.getDesActions().lancer();
        if (partieEnCours.getDesCivilisation().getValeur() == 6) {
            partieEnCours.getDesActions().exploser();
        }
        changementsDesActions.forEach(changement -> changement.traiter(partieEnCours.getDesActions()));
    }

    public <T extends Type> void deployer(Civilisation civilisation, T type) {
        try {
            if (civilisation.getTangible(type).stream().filter(transport -> transport.getType() == type).count() < type.getMaximum()) {
                Region region = civilisation.getCapitale();
                if (region != null) {
                    switch (type) {
                        case TypeArmee typeArmee -> {
                            Armee armee = jabm.creerArmee(civilisation, typeArmee);
                            armee.ajouterParent(partieEnCours);
                            region.getArmees().add(armee);
                            civilisation.getArmees().add(armee);
                            changementsDeploiement.forEach(changement -> changement.traiter(civilisation, armee, region));
                        }
                        case TypeTransport typeTransport -> {
                            Transport transport = jabm.creerTransport(civilisation, typeTransport);
                            transport.ajouterParent(partieEnCours);
                            region.getTransports().add(transport);
                            civilisation.getTransports().add(transport);
                            changementsDeploiement.forEach(changement -> changement.traiter(civilisation, transport, region));
                        }
                        default ->
                                throw new IllegalStateException(MessageFormat.format("Le type {0} ne peut être déployé", type));
                    }
                }
            }
        } catch (JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de déployer une armée", e);
        }
    }

    public void rattacher(Civilisation civilisation, Armee armee, TypeUnite type) {
        civilisation.getReserve()
                .getUnites()
                .stream()
                .filter(unite -> unite.getType() == type)
                .findFirst()
                .ifPresent(unite -> {
                    try {
                        civilisation.getReserve().getUnites().remove(unite);
                        armee.getUnites().add(unite);
                        while (armee.getUnites().stream().mapToInt(element -> element.getType().getForce()).sum() >= (armee.getDesDegats().size() + 1) * 10) {
                            armee.getDesDegats().add(jabm.creerDesDegats());
                        }
                    } catch (JaoExecutionException | JaoParseException e) {
                        LOGGER.error("Impossible d'ajouter un dés de dégâts", e);
                    }
                });
    }

    public void amorcer(Civilisation civilisation) {
        Objects.requireNonNull(civilisation);
        changementsSelectionCivilisation.forEach(changement -> changement.traiter(civilisation));
    }

    public <T extends Type> void amorcer(Tangible<T> objet) {
        Objects.requireNonNull(objet);
        if (Objects.equals(selection, objet)) {
            selection = null;
            changementsDeselection.forEach(ChangementDeselection::traiter);
        } else {
            selection = objet;
            changementsSelection.forEach(changement -> changement.traiter(objet));
        }
    }

    public void deselectionner() {
        if (selection != null) {
            selection = null;
            changementsDeselection.forEach(ChangementDeselection::traiter);
        }
    }

    public void deplacer(Integer ligne, Integer colonne) {
        if (selection != null) {
            switch (selection.getType()) {
                case TypeArmee _ -> {
                    Armee armee = (Armee) selection;
                    partieEnCours.getMonde()
                            .fluxRegions()
                            .filter(region -> region.getArmees().contains(armee))
                            .findFirst()
                            .ifPresent(source -> {
                                Region cible = region(ligne, colonne);
                                source.getArmees().remove(armee);
                                cible.getArmees().add(armee);
                                changementsDeplacement.forEach(changement -> changement.traiter(selection, cible));
                            });
                }
                case TypeTransport _ -> {
                    Transport transport = (Transport) selection;
                    partieEnCours.getMonde()
                            .fluxRegions()
                            .filter(region -> region.getTransports().contains(transport))
                            .findFirst()
                            .ifPresent(region -> {
                                Region cible = region(ligne, colonne);
                                region.getTransports().remove(transport);
                                cible.getTransports().add(transport);
                                changementsDeplacement.forEach(changement -> changement.traiter(selection, cible));
                            });
                }
                default ->
                        throw new IllegalStateException(MessageFormat.format("Le type {0} ne peut pas se déplacer", selection.getType()));
            }
        }
    }

    public <T extends Type> void attaquer(Tangible<T> cible) {
        Objects.requireNonNull(cible);
        if (selection != null && selection != cible) {
            changementsAttaque.forEach(changement -> changement.traiter(selection, cible));
        }
    }

    public void attaquer(Civilisation civilisation) {
        Objects.requireNonNull(civilisation);
        if (selection != null) {
            changementsAttaqueCivilisation.forEach(changement -> changement.traiter(selection, civilisation));
        }
    }

    public void finirTour() {
        changementsFinTour.forEach(ChangementFinTour::traiter);
    }

    public void sauvegarder() {
        partieEnCours.sauvegarder();
    }

    public void souscription(ChangementPartie changement) {
        changementsParties.add(changement);
    }

    public void souscription(ChangementSelectionCivilisation changement) {
        changementsSelectionCivilisation.add(changement);
    }

    public void souscription(ChangementDesCivilisation changement) {
        changementsDesCivilisation.add(changement);
    }

    public void souscription(ChangementDesActions changement) {
        changementsDesActions.add(changement);
    }

    public void souscription(ChangementDeploiement changement) {
        changementsDeploiement.add(changement);
    }

    public void souscription(ChangementDeplacement changement) {
        changementsDeplacement.add(changement);
    }

    public void souscription(ChangementSelection changement) {
        changementsSelection.add(changement);
    }

    public void souscription(ChangementAttaque changement) {
        changementsAttaque.add(changement);
    }

    public void souscription(ChangementDeselection changement) {
        changementsDeselection.add(changement);
    }

    public void souscription(ChangementAttaqueCivilisation changement) {
        changementsAttaqueCivilisation.add(changement);
    }

    public void souscription(ChangementFinTour changement) {
        changementsFinTour.add(changement);
    }

    public Region region(Integer ligne, Integer colonne) {
        return partieEnCours.getMonde().getRegion(ligne, colonne);
    }

    public List<Region> regionsOccuper() {
        return partieEnCours.getMonde()
                .fluxRegions()
                .filter(region -> !region.getArmees().isEmpty() || !region.getTransports().isEmpty())
                .toList();
    }
}
