package org.mgd.guerres.puniques.jeu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.guerres.puniques.coeur.Jabm;
import org.mgd.guerres.puniques.coeur.JabmConnexion;
import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.guerres.puniques.coeur.commun.TypeRegion;
import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.guerres.puniques.coeur.objet.*;
import org.mgd.guerres.puniques.jeu.exception.JeuException;
import org.mgd.guerres.puniques.jeu.souscription.ChangementDesActions;
import org.mgd.guerres.puniques.jeu.souscription.ChangementDesCivilisation;
import org.mgd.guerres.puniques.jeu.souscription.ChangementPartie;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Jeu {
    public static final int NOMBRE_LIGNES = 24;
    public static final int NOMBRE_COLONNES = 36;
    public static final String CIVILISATION_ROMAINE = "Rome";
    public static final String CIVILISATION_CARTHAGINOISE = "Carthage";
    private static final String APPLICATION_JEU_REGIONS_TYPES = "application.jeu.regions.types";
    private static final Logger LOGGER = LogManager.getLogger(Jeu.class);
    private final Path typesRegions;
    private final Jabm jabm;
    private final Registre registre;
    private final LinkedList<ChangementPartie> changementsParties;
    private final LinkedList<ChangementDesCivilisation> changementsDesCivilisation;
    private final LinkedList<ChangementDesActions> changementsDesActions;

    private Partie partieEnCours;

    public Jeu(Properties proprietes) throws JeuException {
        try {
            if (!proprietes.containsKey(APPLICATION_JEU_REGIONS_TYPES)) {
                throw new IllegalStateException(MessageFormat.format("La propriété {0} n''existe pas dans le fichier de configuration.", APPLICATION_JEU_REGIONS_TYPES));
            }

            this.typesRegions = Path.of(proprietes.getProperty(APPLICATION_JEU_REGIONS_TYPES));
            if (!Files.isRegularFile(this.typesRegions)) {
                throw new IllegalStateException(MessageFormat.format("Le chemin {0} doit être un fichier.", this.typesRegions));
            }

            this.jabm = new JabmConnexion(proprietes).ouvrir().getInstance();
            this.registre = this.jabm.registre();
            this.changementsParties = new LinkedList<>();
            this.changementsDesCivilisation = new LinkedList<>();
            this.changementsDesActions = new LinkedList<>();
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

            AtomicInteger ligne = new AtomicInteger(0);
            Files.readAllLines(typesRegions)
                    .forEach(line -> {
                        AtomicInteger colonne = new AtomicInteger(0);
                        Arrays.stream(line.split("\t"))
                                .forEach(types -> {
                                    Region region = partieEnCours.getMonde().getRegion(ligne.get(), colonne.get());
                                    if (types.contains("T")) {
                                        region.getTypes().add(TypeRegion.TERRESTRE);
                                    }
                                    if (types.contains("M")) {
                                        region.getTypes().add(TypeRegion.MARITIME);
                                    }
                                    if (types.contains("C")) {
                                        region.getTypes().add(TypeRegion.CAPITAL);
                                    }
                                    colonne.incrementAndGet();
                                });
                        ligne.incrementAndGet();
                    });

            Civilisation civilisationRomaine = jabm.creerCivilisation(CIVILISATION_ROMAINE,
                    Map.of(
                            TypeArmee.TERRESTRE, 2,
                            TypeArmee.MARITIME, 10
                    ),
                    Map.of(
                            TypeUnite.SOLDAT, 120,
                            TypeUnite.GENERAL, 6,
                            TypeUnite.ELEPHANT, 0,
                            TypeUnite.CATAPULTE, 4
                    ));
            partieEnCours.getCivilisations().add(civilisationRomaine);
            partieEnCours.ajouterEnfant(civilisationRomaine);

            Civilisation civilisationCarthaginoise = jabm.creerCivilisation(CIVILISATION_CARTHAGINOISE,
                    Map.of(
                            TypeArmee.TERRESTRE, 2,
                            TypeArmee.MARITIME, 10
                    ),
                    Map.of(
                            TypeUnite.SOLDAT, 120,
                            TypeUnite.GENERAL, 6,
                            TypeUnite.ELEPHANT, 4,
                            TypeUnite.CATAPULTE, 4
                    ));
            partieEnCours.getCivilisations().add(civilisationCarthaginoise);
            partieEnCours.ajouterEnfant(civilisationCarthaginoise);

            jabm.persister(uuidFichier.toString(), partieEnCours);

            changementsParties.forEach(changementPartie -> changementPartie.traiter(partieEnCours));
        } catch (IOException | JaoExecutionException | JaoParseException e) {
            LOGGER.error("Impossible de créer une nouvelle partie.", e);
        }
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
        System.out.println("Civilisation: " + civilisation + " armée : " + armee);
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
