package org.mgd.guerres.puniques.jeu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.guerres.puniques.coeur.Jabm;
import org.mgd.guerres.puniques.coeur.JabmConnexion;
import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.guerres.puniques.coeur.objet.Civilisation;
import org.mgd.guerres.puniques.coeur.objet.Informations;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.objet.Registre;
import org.mgd.guerres.puniques.jeu.exception.JeuException;
import org.mgd.guerres.puniques.jeu.souscription.ChangementPartie;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Jeu {
    public static final int NOMBRE_LIGNES = 18;
    public static final int NOMBRE_COLONNES = 32;
    public static final String CIVILISATION_ROMAINE = "Rome";
    public static final String CIVILISATION_CARTHAGINOISE = "Carthage";
    private static final Logger LOGGER = LogManager.getLogger(Jeu.class);
    private final Jabm jabm;
    private final Registre registre;
    private final LinkedList<ChangementPartie> changementsParties;

    private Partie partieEnCours;

    public Jeu(Properties proprietes) throws JeuException {
        try {
            this.jabm = new JabmConnexion(proprietes).ouvrir().getInstance();
            this.registre = this.jabm.registre();
            this.changementsParties = new LinkedList<>();
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
            Informations informations = jabm.creerInformations(nouveauNom.get(), uuidFichier, registre);
            registre.sauvegarder();

            partieEnCours = jabm.creerPartie(informations, NOMBRE_LIGNES, NOMBRE_COLONNES);
            partieEnCours.setInformations(informations);

            Civilisation civilisation = jabm.creerCivilisation(CIVILISATION_ROMAINE,
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
            partieEnCours.getCivilisations().add(civilisation);

            civilisation = jabm.creerCivilisation(CIVILISATION_CARTHAGINOISE,
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
            partieEnCours.getCivilisations().add(civilisation);

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

    public boolean avecPartieEnCours() {
        return partieEnCours != null;
    }

    public void souscription(ChangementPartie changementPartie) {
        changementsParties.add(changementPartie);
    }
}
