package org.mgd.guerres.puniques;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.guerres.puniques.coeur.objet.Civilisation;
import org.mgd.guerres.puniques.coeur.objet.Des;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.jeu.Jeu;
import org.mgd.guerres.puniques.jeu.exception.JeuException;
import org.mgd.guerres.puniques.jeu.souscription.ChangementDesActions;
import org.mgd.guerres.puniques.jeu.souscription.ChangementDesCivilisation;
import org.mgd.guerres.puniques.jeu.souscription.ChangementPartie;
import org.mgd.lwjgl.Application;
import org.mgd.lwjgl.PseudoTisseur;
import org.mgd.lwjgl.affichage.element.Cadrillage;
import org.mgd.lwjgl.affichage.element.Jeton;
import org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute;
import org.mgd.lwjgl.affichage.tetehaute.BarreActions;
import org.mgd.lwjgl.affichage.tetehaute.Informations;
import org.mgd.lwjgl.affichage.tetehaute.Menu;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.DetecteurService;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class GuerresPuniquesApplication extends Application {
    private static final Logger LOGGER = LogManager.getLogger(GuerresPuniquesApplication.class);

    private static final String APPLICATION_CONFIGURATION = "application.configuration";
    private static final String APPLICATION_RACINE = "application.racine";

    private static final String IMAGE_DEPLACER = "Déplacer";
    private static final String IMAGE_LANCER_DEX = "Lancer les dés";
    private static final String IMAGE_FINIR_LE_TOUR = "Finir le tour";
    private static final String IMAGE_CIVILISATION_PREFIX = "Civilisation : ";
    private static final String IMAGE_ARMEE_PREFIX = "Armée : ";
    private static final String IMAGE_UNITE_PREFIX = "Unité : ";
    private static final String IMAGE_DEPLOIEMENT_ARMEE_PREFIX = "Déploiement d'une armée : ";

    private static final float PROFONDEUR = -21.5f;

    private static final float BOUTON_TAILLE_POLICE = 48f;
    private static final String BOUTON_NOUVELLE_PARTIE = "Créer et démarrer une nouvelle partie";
    private static final String BOUTON_CHARGER_PARTIE = "Charger une partie";
    private static final String BOUTON_DEMPARRER_PARTIE = "Démarrer une partie";
    private static final String BOUTON_SAUVEGARDER_PARTIE = "Sauvegarder une partie";
    private static final String BOUTON_QUITTER = "Quitter le jeu";

    private final Path dossierTextures;
    private final Path dossierIconesActions;
    private final Path dossierIconesGeneraux;
    private final Jeu jeu;

    private AffichageTeteHaute.NVGPolice police;
    private AffichageTeteHaute.Action actionLancerDex;
    private AffichageTeteHaute.Action actionFinirTour;

    private Cadrillage cadrillage;
    private Jeton jeton;

    private Menu menu;
    private Informations informations;
    private BarreActions barreActions;
    private Informations informationsDesCivilisation;
    private Informations informationDesActions;

    protected GuerresPuniquesApplication() throws LwjglException, IOException {
        super("Guerres puniques", 960, 16, 9);
        Path configuration = Paths.get(System.getProperty(APPLICATION_CONFIGURATION, "./"));
        Path fichier = Files.isRegularFile(configuration) ? configuration : configuration.resolve("configuration.properties");
        try (BufferedReader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            Properties proprietes = new Properties();
            proprietes.load(lecteur);
            this.jeu = new Jeu(proprietes);

            Path dossierRacine = Path.of(proprietes.getProperty(APPLICATION_RACINE));
            this.dossierTextures = dossierRacine.resolve("images\\textures");
            this.dossierIconesActions = dossierRacine.resolve("images\\actions");
            this.dossierIconesGeneraux = this.dossierIconesActions.resolve("generales");
        } catch (JeuException e) {
            throw new LwjglException(e);
        }
    }

    public static void main(String[] args) throws LwjglException, IOException, URISyntaxException {
        new GuerresPuniquesApplication().demarrer();
    }

    @Override
    protected void peupler() throws LwjglException {
        fenetre.creerContexteNvg();
        police = fenetre.creerPolice("Calibri", Path.of("C:\\Windows\\Fonts\\Calibri.ttf"));

        try {
            fenetre.creerImage(IMAGE_DEPLACER, dossierIconesGeneraux.resolve("deplacer.png"));
            fenetre.creerImage(IMAGE_LANCER_DEX, dossierIconesGeneraux.resolve("des.png"));
            fenetre.creerImage(IMAGE_FINIR_LE_TOUR, dossierIconesGeneraux.resolve("fin_de_tour.png"));

            actionLancerDex = new AffichageTeteHaute.Action(100, 100, true, true, fenetre.obtenirImage(IMAGE_LANCER_DEX));
            actionLancerDex.souscrire(index -> jeu.lancerDes());
            actionFinirTour = new AffichageTeteHaute.Action(100, 100, true, true, fenetre.obtenirImage(IMAGE_FINIR_LE_TOUR));
            actionFinirTour.souscrire(index -> System.out.println("Finir le tour"));

            construireMenu().apparaitre();

            jeu.souscription((ChangementPartie) partie -> {
                try {
                    fenetre.affichages().clear();
                    fenetre.enfants().clear();
                    construireInterfaceTeteHaute(partie);
                    construirePlateauJeu();
                    fenetre.basculer();
                    menu.retourPremierePage();
                } catch (LwjglException e) {
                    LOGGER.error("Impossible de construire le jeu", e);
                }
            });
            jeu.souscription((ChangementDesCivilisation) this::majInformationsDesCivilisation);
            jeu.souscription((ChangementDesActions) this::majInformationsDesActions);

            DetecteurService.obtenir().souscrire(fenetre, (cle, code, action, modifications) -> {
                if (cle == GLFW_KEY_ESCAPE && action == GLFW_PRESS && jeu.avecPartieEnCours()) {
                    fenetre.basculer();
                }
            });
        } catch (JeuException e) {
            throw new LwjglException(e);
        }
    }

    private Menu construireMenu() throws LwjglException, JeuException {
        AffichageTeteHaute.Ecrit titre = new AffichageTeteHaute.Ecrit(64f, police, AffichageTeteHaute.BLANC, () -> "Guerres puniques");
        AffichageTeteHaute.Bouton<Void> boutonNouvellePartie = new AffichageTeteHaute.Bouton<>(BOUTON_NOUVELLE_PARTIE, BOUTON_TAILLE_POLICE, police, AffichageTeteHaute.BLANC, () -> "Nouvelle partie", null);
        AffichageTeteHaute.Bouton<Void> boutonCharger = new AffichageTeteHaute.Bouton<>(BOUTON_CHARGER_PARTIE, BOUTON_TAILLE_POLICE, police, AffichageTeteHaute.BLANC, () -> "Charger une partie", null);
        AffichageTeteHaute.Bouton<Void> boutonSauvegarder = new AffichageTeteHaute.Bouton<>(BOUTON_SAUVEGARDER_PARTIE, BOUTON_TAILLE_POLICE, police, AffichageTeteHaute.BLANC, () -> "Sauvegarder la partie", null);
        AffichageTeteHaute.Bouton<Void> boutonQuitter = new AffichageTeteHaute.Bouton<>(BOUTON_QUITTER, BOUTON_TAILLE_POLICE, police, AffichageTeteHaute.BLANC, () -> "Quitter", null);
        menu = new Menu(fenetre, Collections.singleton(titre), Arrays.asList(boutonNouvellePartie, boutonCharger, boutonSauvegarder, boutonQuitter));
        menu.ajouterPage(boutonCharger,
                jeu.registre()
                        .getInformations()
                        .entrySet()
                        .stream()
                        .map(element -> {
                            AffichageTeteHaute.Bouton<UUID> bouton = new AffichageTeteHaute.Bouton<>(BOUTON_DEMPARRER_PARTIE,
                                    48f,
                                    police,
                                    AffichageTeteHaute.BLANC,
                                    () -> element.getValue().getNom(),
                                    element.getKey());
                            bouton.souscrire(jeu::demarrerPartie);
                            return bouton;
                        })
                        .toList(),
                police);
        boutonNouvellePartie.souscrire(objet -> jeu.nouvellePartie());
        boutonSauvegarder.souscrire(objet -> jeu.sauvegarder());
        boutonQuitter.souscrire(objet -> fenetre.fermer());
        return menu;
    }

    private void construireInterfaceTeteHaute(Partie partie) throws LwjglException {
        barreActions = construireBarreActions();

        int ordre = 0;
        for (Civilisation civilisation : partie.getCivilisations()) {
            construireBarreActions(civilisation, ordre++);
        }

        informations = construireInformations();

        Des desCivilisation = partie.getDesCivilisation();
        informationsDesCivilisation = construireDesCivilisation();
        if (desCivilisation.getValeur() != null) {
            majInformationsDesCivilisation(desCivilisation);
        }

        Des desActions = partie.getDesActions();
        informationDesActions = construireDesActions();
        if (desActions.getValeur() != null) {
            majInformationsDesActions(desActions);
        }
    }

    private void majInformationsDesCivilisation(Des desCivilisation) {
        informationsDesCivilisation.afficher(desCivilisation, () -> desCivilisation.getValeur().toString());
    }

    private void majInformationsDesActions(Des desActions) {
        informationDesActions.afficher(desActions, () -> desActions.getValeur().toString());
    }

    private void construirePlateauJeu() throws LwjglException {
        /*jeton = new Jeton(fenetre, "jeton", 0.5f, 0.5f, 0f, 0f, new float[]{-Jeu.NOMBRE_COLONNES / 2f - 1.5f, Jeu.NOMBRE_LIGNES / 2f - 1f, PROFONDEUR}, Map.of(
                ITisseur.PSEUDO_BASE, dossierTextures.resolve("armee_romaine.png")
        ));
        jeton.souscrire(objet -> informations.afficher(jeton, "Jeton"));
        jeton.souscrire(() -> informations.effacer(jeton));*/

        cadrillage = new Cadrillage(fenetre, "carte", Jeu.NOMBRE_COLONNES, Jeu.NOMBRE_LIGNES, PROFONDEUR, Map.of(
                PseudoTisseur.PSEUDO_BASE, dossierTextures.resolve("carte.png"),
                PseudoTisseur.PSEUDO_SURVOLE, dossierTextures.resolve("carte_survoler.png"),
                PseudoTisseur.PSEUDO_ACTIVER, dossierTextures.resolve("carte_activer.png")
        ));
        cadrillage.souscrire(index -> {
            informations.afficher(cadrillage, () -> jeu.region(index[0], index[1]).getInformations());
            barreActions.exposer(cadrillage);
        });
        cadrillage.souscrire(() -> {
            informations.effacer(cadrillage);
            barreActions.cacher(cadrillage);
        });

        barreActions.affilier(cadrillage, actionLancerDex, actionFinirTour);
    }

    private BarreActions construireBarreActions() throws LwjglException {
        return new BarreActions(fenetre,
                List.of(actionLancerDex, actionFinirTour),
                BarreActions.Orientation.HORIZONTAL,
                BarreActions.Justification.DEBUT,
                BarreActions.Ajustement.DEBUT,
                20,
                120,
                fenetre.hauteur() - 110,
                fenetre.largeur() - 240);
    }

    private void construireBarreActions(Civilisation civilisation, int ordre) throws LwjglException {
        fenetre.creerImage(identifiantImageCite(civilisation), cheminImageCite(civilisation));
        Stream.of(TypeUnite.values()).forEach(type -> fenetre.creerImage(identifiantImageUnite(civilisation, type), cheminImageUnite(civilisation, type)));
        Stream.of(TypeArmee.values()).forEach(type -> fenetre.creerImage(identifiantImageArmee(civilisation, type), cheminImageTypeArmee(civilisation, type)));
        fenetre.creerImage(identifiantImageDeploiementArmee(civilisation), cheminImageDeploiementArmee(civilisation));

        AffichageTeteHaute.Action actionCivilisation = new AffichageTeteHaute.Action(100, 100, false, true, fenetre.obtenirImage(identifiantImageCite(civilisation)));

        List<AffichageTeteHaute.Action> actionsArmees = civilisation.getArmees().stream().map(armee -> {
            Arrays.stream(TypeUnite.values())
                    .filter(type -> civilisation.getReserve().getNombresUnitesMaximales().get(type) > 0)
                    .forEach(type -> {
                        AffichageTeteHaute.Action actionArmeeAjouterUnite = new AffichageTeteHaute.Action(100, 100, false, true, fenetre.obtenirImage(identifiantImageUnite(civilisation, type)));
                        actionArmeeAjouterUnite.souscrire(objet -> jeu.rattacherUnite(civilisation, armee, type));
                        barreActions.affilier(armee, actionArmeeAjouterUnite);
                    });
            AffichageTeteHaute.Action actionDeployerArmee = new AffichageTeteHaute.Action(100, 100, false, true, fenetre.obtenirImage(identifiantImageDeploiementArmee(civilisation)));
            actionDeployerArmee.souscrire(objet -> jeu.deployerArmee(civilisation, armee));
            barreActions.affilier(armee, actionDeployerArmee, actionLancerDex, actionFinirTour);

            AffichageTeteHaute.Action action = new AffichageTeteHaute.Action(50, 50, true, false, fenetre.obtenirImage(identifiantImageArmee(civilisation, armee.getType())));
            action.souscrire(index -> {
                civilisation.getArmees().stream().filter(autreArmee -> autreArmee != armee).forEach(informations::effacer);
                informations.afficher(armee, armee::getInformations);
                barreActions.exposer(armee);
            });
            return action;
        }).toList();
        List<AffichageTeteHaute.Action> actions = Stream.concat(Stream.of(actionCivilisation), actionsArmees.stream()).toList();

        BarreActions barreActionsCivilisations = new BarreActions(fenetre,
                actions,
                BarreActions.Orientation.VERTICAL,
                BarreActions.Justification.DEBUT,
                BarreActions.Ajustement.CENTRAL,
                30,
                ordre == 0 ? 10 : fenetre.largeur() - 110,
                10,
                fenetre.hauteur() - 130);
        barreActionsCivilisations.souscrire(() -> {
            civilisation.getArmees().forEach(armee -> {
                informations.effacer(armee);
                barreActions.cacher(armee);
            });
            informations.effacer(barreActionsCivilisations);
            barreActions.cacher(barreActionsCivilisations);
        });

        actionCivilisation.souscrire(index -> {
            barreActionsCivilisations.defiger();
            civilisation.getArmees().forEach(informations::effacer);
            informations.afficher(barreActionsCivilisations, civilisation::getInformations);
            barreActions.exposer(barreActionsCivilisations);
        });
        actionsArmees.forEach(action -> action.souscrire(index -> barreActionsCivilisations.figer()));

        barreActions.souscrire(barreActionsCivilisations::defiger);
    }

    private Informations construireInformations() throws LwjglException {
        return new Informations(fenetre, 48f, police, AffichageTeteHaute.BLANC, 120, 0, fenetre.largeur() - 240, 0, false);
    }

    private Informations construireDesCivilisation() throws LwjglException {
        return new Informations(fenetre, 48f, police, AffichageTeteHaute.BLANC, 10, fenetre.hauteur() - 110, 100, 100, true);
    }

    private Informations construireDesActions() throws LwjglException {
        return new Informations(fenetre, 48f, police, AffichageTeteHaute.BLANC, fenetre.largeur() - 110, fenetre.hauteur() - 110, 100, 100, true);
    }

    private String identifiantImageCite(Civilisation civilisation) {
        return IMAGE_CIVILISATION_PREFIX + civilisation.getNom();
    }

    private String identifiantImageArmee(Civilisation civilisation, TypeArmee type) {
        return IMAGE_ARMEE_PREFIX + String.join(",", civilisation.getNom(), type.name());
    }

    private String identifiantImageUnite(Civilisation civilisation, TypeUnite type) {
        return IMAGE_UNITE_PREFIX + String.join(",", civilisation.getNom(), type.name());
    }

    private String identifiantImageDeploiementArmee(Civilisation civilisation) {
        return IMAGE_DEPLOIEMENT_ARMEE_PREFIX + civilisation.getNom();
    }

    private Path cheminImageCite(Civilisation civilisation) {
        return dossierIconesActions.resolve("civilisations").resolve(civilisation.getNom().toLowerCase()).resolve("cite.png");
    }

    private Path cheminImageArmee(Civilisation civilisation) {
        return dossierIconesActions.resolve("armees").resolve(civilisation.getNom().toLowerCase());
    }

    private Path cheminImageTypeArmee(Civilisation civilisation, TypeArmee type) {
        return cheminImageArmee(civilisation).resolve(type.name().toLowerCase() + ".png");
    }

    private Path cheminImageUnite(Civilisation civilisation, TypeUnite type) {
        return cheminImageArmee(civilisation).resolve(type.name().toLowerCase() + ".png");
    }

    private Path cheminImageDeploiementArmee(Civilisation civilisation) {
        return cheminImageArmee(civilisation).resolve("deployer.png");
    }
}