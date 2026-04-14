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
    private final LinkedList<ChangementDesCivilisation> changementsDesCivilisation;
    private final LinkedList<ChangementDesActions> changementsDesActions;
    private final LinkedList<ChangementDeploiementArmee> changementsDeploiementArmee;
    private final LinkedList<ChangementDeplacementArmee> changementsDeplacementArmee;
    private final LinkedList<ChangementSelectionArmee> changementsSelectionArmee;
    private final LinkedList<ChangementDeselectionArmee> changementsDeselectionArmee;
    private final LinkedList<ChangementAttaqueArmee> changementsAttaqueArmee;
    private final LinkedList<ChangementAttaqueCivilisation> changementsAttaqueCivilisation;
    private final LinkedList<ChangementFinTour> changementsFinTour;
    private final String[] aliass;

    private Partie partieEnCours;
    private Armee armeeSelectionnee;

    public Jeu(Properties proprietes) throws JeuException {
        try {
            this.proprietes = proprietes;
            this.jabm = new JabmConnexion(proprietes).ouvrir().getInstance();
            this.registre = this.jabm.registre();
            this.changementsParties = new LinkedList<>();
            this.changementsDesCivilisation = new LinkedList<>();
            this.changementsDesActions = new LinkedList<>();
            this.changementsDeploiementArmee = new LinkedList<>();
            this.changementsDeplacementArmee = new LinkedList<>();
            this.changementsSelectionArmee = new LinkedList<>();
            this.changementsDeselectionArmee = new LinkedList<>();
            this.changementsAttaqueArmee = new LinkedList<>();
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

            List<TypeRegion> typeRegions = Arrays.stream(obtenirTypesRegions()).map(nom -> {
                try {
                    return jabm.creerTypeRegion(obtenirCodeTypeRegion(nom));
                } catch (JaoExecutionException | JaoParseException e) {
                    LOGGER.error("Impossible de construire le type de région {}.", nom, e);
                    return null;
                }
            }).filter(Objects::nonNull).toList();

            partieEnCours = jabm.creerPartie(informations, typeRegions, taille);
            partieEnCours.setInformations(informations);

            Map<String, Civilisation> civilisations = new HashMap<>();
            Arrays.stream(aliass).forEach(alias -> {
                try {
                    List<TypeUnite> typesUnites = fluxTypes(alias, NOM_GROUPE_TYPES_UNITES).map(type -> nouveauTypeUnite(alias, type, typeRegions)).filter(Objects::nonNull).toList();
                    List<TypeTransport> typesTransports = fluxTypes(alias, NOM_GROUPE_TYPES_TRANSPORTS).map(type -> nouveauTypeTransport(alias, type, typeRegions)).filter(Objects::nonNull).toList();
                    List<TypeArmee> typeArmees = fluxTypes(alias, NOM_GROUPE_TYPES_ARMEES).map(type -> nouveauTypeArmee(alias, type)).filter(Objects::nonNull).toList();
                    Civilisation civilisation = jabm.creerCivilisation(obtenirProprieteCivilisation(alias, "nom"),
                            typesUnites,
                            typesTransports,
                            typeArmees,
                            partieEnCours.getMonde().getRegion(obtenirIndexCapitaleCivilisation(alias)));
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
                } catch (JaoExecutionException | JaoParseException e) {
                    LOGGER.error("Impossible d'ajouter les alignements", e);
                }
            });

            jabm.persister(uuidFichier.toString(), partieEnCours);

            changementsParties.forEach(changement -> changement.traiter(partieEnCours));
            armeeSelectionnee = null;
        } catch (IOException | JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de créer une nouvelle partie.", e);
        }
    }

    private TypeArmee nouveauTypeArmee(String alias, String type) {
        try {
            TypeArmee typeTransport = jabm.creerTypeArmee(type,
                    obtenirTextureType(alias, NOM_GROUPE_TYPES_ARMEES, type),
                    obtenirLibelleType(alias, NOM_GROUPE_TYPES_ARMEES, type),
                    obtenirMaximumType(alias, NOM_GROUPE_TYPES_ARMEES, type));
            partieEnCours.ajouterEnfant(typeTransport);
            return typeTransport;
        } catch (JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de construire le type d'armée {} de la civilisation {}.", type, alias, e);
            return null;
        }
    }

    private TypeTransport nouveauTypeTransport(String alias, String type, List<TypeRegion> typeRegions) {
        try {
            TypeTransport typeTransport = jabm.creerTypeTransport(type,
                    obtenirPraticablesType(alias, NOM_GROUPE_TYPES_TRANSPORTS, type, typeRegions),
                    obtenirTextureType(alias, NOM_GROUPE_TYPES_TRANSPORTS, type),
                    obtenirLibelleType(alias, NOM_GROUPE_TYPES_TRANSPORTS, type),
                    obtenirMaximumType(alias, NOM_GROUPE_TYPES_TRANSPORTS, type));
            partieEnCours.ajouterEnfant(typeTransport);
            return typeTransport;
        } catch (JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de construire le type de transport {} de la civilisation {}.", type, alias, e);
            return null;
        }
    }

    private TypeUnite nouveauTypeUnite(String alias, String type, List<TypeRegion> typeRegions) {
        try {
            TypeUnite typeUnite = jabm.creerTypeUnite(type,
                    obtenirPraticablesType(alias, NOM_GROUPE_TYPES_UNITES, type, typeRegions),
                    obtenirLibelleType(alias, NOM_GROUPE_TYPES_UNITES, type),
                    obtenirMaximumType(alias, NOM_GROUPE_TYPES_UNITES, type),
                    obtenirNombreType(alias, NOM_GROUPE_TYPES_UNITES, type, "vie"),
                    obtenirNombreType(alias, NOM_GROUPE_TYPES_UNITES, type, "force"));
            partieEnCours.ajouterEnfant(typeUnite);
            return typeUnite;
        } catch (JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de construire le type d'unité {} de la civilisation {}.", type, alias, e);
            return null;
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
                .map(praticable -> typeRegions.stream().filter(typeRegion -> Objects.equals(praticable, typeRegion.getCode())).findFirst().orElse(null))
                .filter(Objects::nonNull)
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

    public void deployerArmee(Civilisation civilisation, Armee armee) {
        if (partieEnCours.getMonde().fluxRegions().noneMatch(region -> region.getArmees().contains(armee))) {
            Optional.ofNullable(civilisation.getCapitale()).ifPresent(region -> {
                region.getArmees().add(armee);
                changementsDeploiementArmee.forEach(changement -> changement.traiter(armee, region));
            });
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

    public void amorcer(Armee armee) {
        Objects.requireNonNull(armee);
        if (Objects.equals(armeeSelectionnee, armee)) {
            armeeSelectionnee = null;
            changementsDeselectionArmee.forEach(ChangementDeselectionArmee::traiter);
        } else {
            armeeSelectionnee = armee;
            changementsSelectionArmee.forEach(changement -> changement.traiter(armee));
        }
    }

    public void deselectionner() {
        if (armeeSelectionnee != null) {
            armeeSelectionnee = null;
            changementsDeselectionArmee.forEach(ChangementDeselectionArmee::traiter);
        }
    }

    public void deplacer(Integer ligne, Integer colonne) {
        if (armeeSelectionnee != null) {
            partieEnCours.getMonde()
                    .fluxRegions()
                    .filter(region -> region.getArmees().contains(armeeSelectionnee)
                            && !Objects.equals(region.ligne(), ligne)
                            && !Objects.equals(region.colonne(), colonne))
                    .findFirst()
                    .ifPresent(region -> {
                        region.getArmees().remove(armeeSelectionnee);
                        Region cible = region(ligne, colonne);
                        cible.getArmees().add(armeeSelectionnee);
                        changementsDeplacementArmee.forEach(changement -> changement.traiter(armeeSelectionnee, cible));
                    });
        }
    }

    public void attaquer(Armee armee) {
        Objects.requireNonNull(armee);
        if (armeeSelectionnee != null && armeeSelectionnee != armee) {
            changementsAttaqueArmee.forEach(changement -> changement.traiter(armeeSelectionnee, armee));
        }
    }

    public void attaquer(Civilisation civilisation) {
        Objects.requireNonNull(civilisation);
        if (armeeSelectionnee != null) {
            changementsAttaqueCivilisation.forEach(changement -> changement.traiter(armeeSelectionnee, civilisation));
        }
    }

    public void finirTour() {
        changementsFinTour.forEach(ChangementFinTour::traiter);
    }

    public void sauvegarder() {
        partieEnCours.sauvegarder();
    }

    public boolean avecPartieEnCours() {
        return Objects.nonNull(partieEnCours);
    }

    public void souscription(ChangementPartie changementPartie) {
        changementsParties.add(changementPartie);
    }

    public void souscription(ChangementDesCivilisation changement) {
        changementsDesCivilisation.add(changement);
    }

    public void souscription(ChangementDesActions changement) {
        changementsDesActions.add(changement);
    }

    public void souscription(ChangementDeploiementArmee changement) {
        changementsDeploiementArmee.add(changement);
    }

    public void souscription(ChangementDeplacementArmee changement) {
        changementsDeplacementArmee.add(changement);
    }

    public void souscription(ChangementSelectionArmee changement) {
        changementsSelectionArmee.add(changement);
    }

    public void souscription(ChangementAttaqueArmee changement) {
        changementsAttaqueArmee.add(changement);
    }

    public void souscription(ChangementDeselectionArmee changement) {
        changementsDeselectionArmee.add(changement);
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

    public Stream<Region> fluxRegionsOccuper() {
        return partieEnCours.getMonde().fluxRegions().filter(region -> !region.getArmees().isEmpty());
    }
}
