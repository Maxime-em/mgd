package org.mgd.guerres.puniques.jeu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.guerres.puniques.coeur.Jabm;
import org.mgd.guerres.puniques.coeur.JabmConnexion;
import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
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
    public static final int NOMBRE_LIGNES = 24;
    public static final int NOMBRE_COLONNES = 36;
    public static final String APPLICATION_JEU_CIVILISATIONS = "application.jeu.civilisations";

    private static final String APPLICATION_JEU_REGIONS_TYPES = "application.jeu.regions.types";
    private static final String APPLICATION_JEU_REGIONS_ALIGNEMENTS = "application.jeu.regions.alignements";
    private static final Logger LOGGER = LogManager.getLogger(Jeu.class);

    private final Properties proprietes;
    private final Path typesRegions;
    private final Path alignementsRegions;
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
    private final String[] aliasCivilisations;

    private Partie partieEnCours;
    private Armee armeeSelectionnee;

    public Jeu(Properties proprietes) throws JeuException {
        try {
            this.proprietes = proprietes;
            this.typesRegions = obtenirChemin(proprietes, APPLICATION_JEU_REGIONS_TYPES);
            this.alignementsRegions = obtenirChemin(proprietes, APPLICATION_JEU_REGIONS_ALIGNEMENTS);
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
            this.aliasCivilisations = obtenirAliasCivilisations();
        } catch (ConnexionException | IOException | JaoExecutionException | JaoParseException e) {
            throw new JeuException(e);
        }
    }

    public String[] obtenirAliasCivilisations() {
        return ((String) proprietes.getOrDefault(APPLICATION_JEU_CIVILISATIONS, "")).split(";");
    }

    public String obtenirNomCivilisation(String alias) {
        return obtenirProprieteCivilisations(alias, "nom");
    }

    private Path obtenirChemin(Properties proprietes, String nom) {
        if (!proprietes.containsKey(nom)) {
            throw new IllegalStateException(MessageFormat.format("La propriété {0} n''existe pas dans le fichier de configuration.", nom));
        }
        Path chemin = Path.of(proprietes.getProperty(nom));
        if (!Files.isRegularFile(chemin)) {
            throw new IllegalStateException(MessageFormat.format("Le chemin {0} doit être un fichier.", chemin));
        }
        return chemin;
    }

    public Registre registre() throws JeuException {
        try {
            return jabm.registre();
        } catch (IOException | JaoExecutionException | JaoParseException e) {
            throw new JeuException(e);
        }
    }

    public void nouvellePartie() {
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

            partieEnCours = jabm.creerPartie(informations, NOMBRE_LIGNES, NOMBRE_COLONNES);
            partieEnCours.setInformations(informations);

            Map<String, Civilisation> civilisations = new HashMap<>();
            Arrays.stream(aliasCivilisations).forEach(alias -> {
                try {
                    List<TypeUnite> types = Arrays.stream(obtenirTypesUnites(alias).split(";")).map(nom -> {
                        try {
                            TypeUnite typeUnite = jabm.creerTypeUnite(nom,
                                    obtenirTypeUnite(alias, nom, "libelle"),
                                    obtenirNombreTypeUnite(alias, nom, "maximum"),
                                    obtenirNombreTypeUnite(alias, nom, "vie"),
                                    obtenirNombreTypeUnite(alias, nom, "force"));
                            partieEnCours.ajouterEnfant(typeUnite);
                            return typeUnite;
                        } catch (JaoExecutionException | JaoParseException e) {
                            LOGGER.error("Impossible de construire le type d'unité {} de la civilisation {}.", nom, alias, e);
                            return null;
                        }
                    }).filter(Objects::nonNull).toList();
                    Civilisation civilisation = jabm.creerCivilisation(obtenirProprieteCivilisations(alias, "nom"),
                            Map.of(
                                    TypeArmee.TERRESTRE, obtenirNombreArmees(alias, "terrestres"),
                                    TypeArmee.MARITIME, obtenirNombreArmees(alias, "maritime")
                            ),
                            types);
                    partieEnCours.getCivilisations().add(civilisation);
                    partieEnCours.ajouterEnfant(civilisation);
                    civilisations.put(obtenirProprieteCivilisations(alias, "code"), civilisation);
                } catch (JaoExecutionException | JaoParseException | NumberFormatException e) {
                    LOGGER.error("Impossible de construire la civilisation {}.", alias, e);
                }
            });

            parcourirRegion(typesRegions, Region::ajouterTypes);
            parcourirRegion(alignementsRegions, (Region region, String codes) -> {
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

    private void parcourirRegion(Path fichier, BiConsumer<Region, String> action) {
        Fichiers.parcourir(fichier, (ligne, colonne) -> partieEnCours.getMonde().getRegion(ligne, colonne), action);
    }

    private String obtenirProprieteCivilisations(String alias, String nom) {
        return proprietes.getProperty(MessageFormat.format("{0}.{1}.{2}", APPLICATION_JEU_CIVILISATIONS, alias, nom));
    }

    private String obtenirTypesUnites(String alias) {
        return proprietes.getProperty(MessageFormat.format("{0}.{1}.unites.types", APPLICATION_JEU_CIVILISATIONS, alias));
    }

    private String obtenirTypeUnite(String alias, String type, String nom) {
        return proprietes.getProperty(MessageFormat.format("{0}.{1}.unites.{2}.{3}", APPLICATION_JEU_CIVILISATIONS, alias, type, nom));
    }

    private Integer obtenirNombreTypeUnite(String alias, String type, String nom) {
        return Integer.valueOf(obtenirTypeUnite(alias, type, nom));
    }

    private Integer obtenirNombreArmees(String alias, String type) {
        return Integer.valueOf(proprietes.getProperty(MessageFormat.format("{0}.{1}.armees.{2}", APPLICATION_JEU_CIVILISATIONS, alias, type)));
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
            partieEnCours.getMonde()
                    .fluxRegions()
                    .filter(region -> region.estAmiAvec(civilisation))
                    .findFirst()
                    .ifPresent(region -> {
                        region.getArmees().add(armee);
                        changementsDeploiementArmee.forEach(changement -> changement.traiter(civilisation, armee, region));
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
