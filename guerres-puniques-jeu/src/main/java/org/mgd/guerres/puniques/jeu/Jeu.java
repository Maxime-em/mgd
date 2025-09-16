package org.mgd.guerres.puniques.jeu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.commun.BiConsommateur;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.guerres.puniques.coeur.Jabm;
import org.mgd.guerres.puniques.coeur.JabmConnexion;
import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.guerres.puniques.coeur.objet.*;
import org.mgd.guerres.puniques.jeu.exception.JeuException;
import org.mgd.guerres.puniques.jeu.souscription.ChangementDeploiementArmee;
import org.mgd.guerres.puniques.jeu.souscription.ChangementDesActions;
import org.mgd.guerres.puniques.jeu.souscription.ChangementDesCivilisation;
import org.mgd.guerres.puniques.jeu.souscription.ChangementPartie;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.utilitaire.Fichiers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Jeu {
    public static final int NOMBRE_LIGNES = 24;
    public static final int NOMBRE_COLONNES = 36;
    private static final String APPLICATION_JEU_REGIONS_TYPES = "application.jeu.regions.types";
    private static final String APPLICATION_JEU_REGIONS_ALIGNEMENTS = "application.jeu.regions.alignements";
    private static final String APPLICATION_JEU_CIVILISATIONS = "application.jeu.civilisations";
    private static final Logger LOGGER = LogManager.getLogger(Jeu.class);
    private final Properties properties;
    private final Path typesRegions;
    private final Path alignementsRegions;
    private final Jabm jabm;
    private final Registre registre;
    private final LinkedList<ChangementPartie> changementsParties;
    private final LinkedList<ChangementDesCivilisation> changementsDesCivilisation;
    private final LinkedList<ChangementDesActions> changementsDesActions;
    private final LinkedList<ChangementDeploiementArmee> changementsDeploiementArmee;

    private Partie partieEnCours;

    public Jeu(Properties proprietes) throws JeuException {
        try {
            this.properties = proprietes;
            this.typesRegions = obtenirChemin(proprietes, APPLICATION_JEU_REGIONS_TYPES);
            this.alignementsRegions = obtenirChemin(proprietes, APPLICATION_JEU_REGIONS_ALIGNEMENTS);
            this.jabm = new JabmConnexion(proprietes).ouvrir().getInstance();
            this.registre = this.jabm.registre();
            this.changementsParties = new LinkedList<>();
            this.changementsDesCivilisation = new LinkedList<>();
            this.changementsDesActions = new LinkedList<>();
            this.changementsDeploiementArmee = new LinkedList<>();
        } catch (ConnexionException | IOException | JaoExecutionException | JaoParseException e) {
            throw new JeuException(e);
        }
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
            Arrays.stream(((String) properties.getOrDefault(APPLICATION_JEU_CIVILISATIONS, "")).split(";"))
                    .forEach(alias -> {
                        try {
                            Civilisation civilisation = jabm.creerCivilisation(obtenirProprieteCivilisations(alias, "nom"),
                                    Map.of(
                                            TypeArmee.TERRESTRE, obtenirNombreArmeesCivilisations(alias, "terrestres"),
                                            TypeArmee.MARITIME, obtenirNombreArmeesCivilisations(alias, "maritime")
                                    ),
                                    Map.of(
                                            TypeUnite.SOLDAT, obtenirUniteReserveCivilisations(alias, "soldat"),
                                            TypeUnite.GENERAL, obtenirUniteReserveCivilisations(alias, "general"),
                                            TypeUnite.ELEPHANT, obtenirUniteReserveCivilisations(alias, "elephant"),
                                            TypeUnite.CATAPULTE, obtenirUniteReserveCivilisations(alias, "catapulte")
                                    ));
                            partieEnCours.getCivilisations().add(civilisation);
                            partieEnCours.ajouterEnfant(civilisation);
                            civilisations.put(obtenirProprieteCivilisations(alias, "code"), civilisation);
                        } catch (JaoExecutionException | JaoParseException | NumberFormatException e) {
                            LOGGER.error("Impossible de construire la civilisation {}.", alias, e);
                        }
                    });

            parcourirRegion(typesRegions, Region::ajouterTypes);
            parcourirRegion(alignementsRegions, (Region region, String codes) -> region.ajouterAlignementAmi(codes, civilisations));

            jabm.persister(uuidFichier.toString(), partieEnCours);

            changementsParties.forEach(changementPartie -> changementPartie.traiter(partieEnCours));
        } catch (IOException | JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de créer une nouvelle partie.", e);
        }
    }

    private void parcourirRegion(Path fichier, BiConsommateur<Region, String> action) throws IOException {
        Fichiers.parcourir(fichier, (ligne, colonne) -> partieEnCours.getMonde().getRegion(ligne, colonne), action);
    }

    private String obtenirProprieteCivilisations(String alias, String nom) {
        return properties.getProperty(MessageFormat.format("{0}.{1}.{2}", APPLICATION_JEU_CIVILISATIONS, alias, nom));
    }

    private Integer obtenirNombreArmeesCivilisations(String alias, String type) {
        return Integer.valueOf(properties.getProperty(MessageFormat.format("{0}.{1}.armees.{2}", APPLICATION_JEU_CIVILISATIONS, alias, type)));
    }

    private Integer obtenirUniteReserveCivilisations(String alias, String type) {
        return Integer.valueOf(properties.getProperty(MessageFormat.format("{0}.{1}.reserve.{2}", APPLICATION_JEU_CIVILISATIONS, alias, type)));
    }

    public void demarrerPartie(UUID uuidFichier) {
        try {
            partieEnCours = jabm.partie(uuidFichier);
            changementsParties.forEach(changementPartie -> changementPartie.traiter(partieEnCours));
        } catch (IOException | JaoExecutionException | JaoParseException e) {
            LOGGER.error(MessageFormat.format("Impossible de démarrer la partie {0}.", uuidFichier), e);
        }
    }

    public void lancerDes() {
        partieEnCours.getDesCivilisation().lancer();
        changementsDesCivilisation.forEach(changementDesCivilisation -> changementDesCivilisation.traiter(partieEnCours.getDesCivilisation()));
        partieEnCours.getDesActions().lancer();
        if (partieEnCours.getDesCivilisation().getValeur() == 6) {
            partieEnCours.getDesActions().exploser();
        }
        changementsDesActions.forEach(changementDesActions -> changementDesActions.traiter(partieEnCours.getDesActions()));
    }

    public void deployerArmee(Civilisation civilisation, Armee armee) {
        partieEnCours.getMonde()
                .fluxRegions()
                .filter(region -> region.estAmiAvec(civilisation))
                .findFirst()
                .ifPresent(region -> {
                    region.getArmees().add(armee);
                    civilisation.getArmees().remove(armee);
                    changementsDeploiementArmee.forEach(changementArmee -> changementArmee.traiter(civilisation, armee, region));
                });
    }

    public void sauvegarder() {
        partieEnCours.sauvegarder();
    }

    public boolean avecPartieEnCours() {
        return partieEnCours != null;
    }

    public void souscription(ChangementPartie changementPartie) {
        changementsParties.add(changementPartie);
    }

    public void souscription(ChangementDesCivilisation changementDesCivilisation) {
        changementsDesCivilisation.add(changementDesCivilisation);
    }

    public void souscription(ChangementDesActions changementDesActions) {
        changementsDesActions.add(changementDesActions);
    }

    public void souscription(ChangementDeploiementArmee changementDeploiementArmee) {
        changementsDeploiementArmee.add(changementDeploiementArmee);
    }

    public void rattacherUnite(Civilisation civilisation, Armee armee, TypeUnite type) {
        civilisation.getReserve()
                .getUnites()
                .stream()
                .filter(unite -> unite.getType() == type)
                .findFirst()
                .ifPresent(unite -> {
                    civilisation.getReserve().getUnites().remove(unite);
                    armee.getUnites().add(unite);
                });
    }

    public Region region(Integer ligne, Integer colonne) {
        return partieEnCours.getMonde().getRegion(ligne, colonne);
    }
}
