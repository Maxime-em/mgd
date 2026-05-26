package org.mgd.guerres.puniques;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.guerres.puniques.coeur.objet.*;
import org.mgd.guerres.puniques.jeu.Jeu;
import org.mgd.guerres.puniques.jeu.exception.JeuException;
import org.mgd.guerres.puniques.jeu.souscription.*;
import org.mgd.lwjgl.Application;
import org.mgd.lwjgl.Programme;
import org.mgd.lwjgl.Pseudo;
import org.mgd.lwjgl.affichage.element.Cadrillage;
import org.mgd.lwjgl.affichage.element.forme.Forme;
import org.mgd.lwjgl.affichage.tetehaute.*;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Alignement;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Dimensionnement;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Justification;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Orientation;
import org.mgd.lwjgl.affichage.tetehaute.Informations;
import org.mgd.lwjgl.affichage.tetehaute.composant.ActionImagee;
import org.mgd.lwjgl.affichage.tetehaute.composant.ActionTextutelle;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGPolice;
import org.mgd.lwjgl.commun.Identifiable;
import org.mgd.lwjgl.exception.LwjglException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute.BLANC;

public class GuerresPuniquesApplication extends Application {
    private static final Logger LOGGER = LogManager.getLogger(GuerresPuniquesApplication.class);

    private static final String APPLICATION_CONFIGURATION = "application.configuration";
    private static final String APPLICATION_RACINE = "application.racine";
    private static final String APPLICATION_OMBRAGES = "application.ombrages";
    private static final String APPLICATION_TAILLE_CADRILLAGE = "application.jeu.cadrillage.taille";
    private static final String APPLICATION_TAILLE_JETONS = "application.jeu.jetons.taille";
    private static final String IMAGE_LANCER_DEX = "Lancer les dés";
    private static final String IMAGE_FINIR_LE_TOUR = "Finir le tour";
    private static final String IMAGE_CIVILISATION_PREFIX = "Civilisation";
    private static final String IMAGE_DEPLOIEMENT_ARMEE_PREFIX = "Déploiement d'une armée";
    private static final String POLICE_DEFAUT = "Calibri";
    private static final float PROFONDEUR = -21.5f;
    private static final float BOUTON_TAILLE_POLICE = 48f;

    private final Jeu jeu;
    private final Path dossierTextures;
    private final int[] tailleCadrillage;
    private final int[] tailleJetons;
    private final Identificateur<UUID, ActionTextutelle<UUID>> actionsPartiesCharger;
    private final Identificateur<Civilisation, ActionImagee<Civilisation>> actionsCivilisations;
    private final Identificateur<Armee, ActionImagee<CivilisationArmee>> actionsArmees;
    private final BiIdentificateur<Armee, TypeUnite, ActionImagee<CivilisationArmeeTypeUnite>> actionsArmeesAjouterUnite;
    private final Identificateur<Armee, ActionImagee<CivilisationArmee>> actionsArmeesDeployer;
    private final Identificateur<Armee, ActionTextutelle<Armee>> actionsArmeesAttaquer;
    private final Identificateur<Armee, Forme> jetonsArmees;
    private final Identificateur<Integer[], Forme> cases;
    private final Map<Civilisation, BarreActions<String>> barresActionsCivilisations;
    private final Map<Armee, ListeActions<Armee>> listesActionsArmees;
    private ActionImagee<Void> lancerDes;
    private ActionImagee<Void> finirTour;
    private ActionTextutelle<Void> nouvellePartie;
    private ActionTextutelle<Void> sauvegarder;
    private ActionTextutelle<Void> quitter;
    private Cadrillage cadrillage;
    private Menu menu;
    private BarreActions<UUID> barreActionsGenerale;
    private Informations informationsDesCivilisation;
    private Informations informationDesActions;
    private Partie partie;

    protected GuerresPuniquesApplication() throws LwjglException, IOException {
        super("Guerres puniques", 960, 16, 9);
        LOGGER.info("Récupération du chemin du fichier de configuration.");
        Path configuration = Paths.get(System.getProperty(APPLICATION_CONFIGURATION, "./"));
        Path fichier = Files.isRegularFile(configuration) ? configuration : configuration.resolve("configuration.properties");
        LOGGER.info("Lecture du fichier de configuration : {}.", fichier.toAbsolutePath());
        try (BufferedReader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            Properties proprietes = new Properties();
            proprietes.load(lecteur);

            LOGGER.info("Création du moteur de jeu.");
            this.jeu = new Jeu(proprietes);

            LOGGER.info("Récupération du chemin du dossier racine.");
            Path dossierRacine = Path.of(proprietes.getProperty(APPLICATION_RACINE));

            LOGGER.info("Configuration interne de l'application.");
            configurer(dossierRacine);

            LOGGER.info("Chargement des programmes d'ombrages de l'application.");
            for (String ombrage : proprietes.getProperty(APPLICATION_OMBRAGES).split(",")) {
                String[] description = ombrage.split("#");
                String[] selecteur = description[0].split(":");
                Programme.nouveau(
                        selecteur[0],
                        selecteur[1],
                        dossierRacine,
                        description[1].split(":"),
                        description[2].split(":"));
            }

            LOGGER.info("Autre configuration de l'application.");
            this.dossierTextures = dossierRacine.resolve("textures");
            this.tailleCadrillage = Arrays.stream(proprietes.getProperty(APPLICATION_TAILLE_CADRILLAGE).split(";")).mapToInt(Integer::parseInt).toArray();
            this.tailleJetons = Arrays.stream(proprietes.getProperty(APPLICATION_TAILLE_JETONS).split(";")).mapToInt(Integer::parseInt).toArray();
            this.actionsPartiesCharger = new Identificateur<>();
            this.actionsCivilisations = new Identificateur<>();
            this.actionsArmees = new Identificateur<>();
            this.actionsArmeesAjouterUnite = new BiIdentificateur<>();
            this.actionsArmeesDeployer = new Identificateur<>();
            this.actionsArmeesAttaquer = new Identificateur<>();
            this.jetonsArmees = new Identificateur<>();
            this.cases = new Identificateur<>();
            this.barresActionsCivilisations = new HashMap<>();
            this.listesActionsArmees = new HashMap<>();
        } catch (JeuException e) {
            throw new LwjglException(e);
        }
    }

    static void main() {
        try {
            new GuerresPuniquesApplication().demarrer();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private static ActionTextutelle<Void> creerInformations(NVGPolice police, Supplier<String> texte) {
        return new ActionTextutelle<>(24f, police, BLANC, texte);
    }

    public static <G, T> void formatterInformations(BarreActions<G> barreActions, ActionImagee<T> actionImagee, NVGPolice police, Supplier<String> texte) {
        barreActions.ajouter(actionImagee.uuid(), new ActionTextutelle<>(24f, police, BLANC, texte));
    }

    private static ActionTextutelle<Void> creerValeurDes(NVGPolice police, Supplier<String> texte) {
        return new ActionTextutelle<>(48f, police, BLANC, texte);
    }

    @Override
    protected void peupler() throws LwjglException {
        fenetre.creerContexteNvg();
        AffichageTeteHaute.creerPolice(fenetre.contexteNvg(), POLICE_DEFAUT, Path.of("C:\\Windows\\Fonts\\Calibri.ttf"));
        AffichageTeteHaute.creerImage(fenetre.contexteNvg(), IMAGE_LANCER_DEX, dossierTextures.resolve("generales", "des.png"));
        AffichageTeteHaute.creerImage(fenetre.contexteNvg(), IMAGE_FINIR_LE_TOUR, dossierTextures.resolve("generales", "fin_de_tour.png"));

        try {
            construireMenu();
            construireJeu();
            menu.apparaitre();
        } catch (JeuException e) {
            throw new LwjglException(e);
        }
    }

    private void placer(Armee armee, Region region) throws LwjglException {
        Forme jeton = cadrillage.ajouterJeton(region.ligne(), region.colonne(), armee.getType().ligne(), armee.getType().colonne());
        actionsArmees.identifiable(armee).lier(jeton);
        jetonsArmees.ajouter(armee, jeton);

        ActionTextutelle<Armee> actionAttaqueArmee = new ActionTextutelle<>(armee, 24f, AffichageTeteHaute.obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT), BLANC, () -> "Attaquer");
        actionsArmeesAttaquer.ajouter(armee, actionAttaqueArmee);

        ListeActions<Armee> listeActionsArmee = new ListeActions<>(fenetre, 10, 10, actionAttaqueArmee);
        listeActionsArmee.lier(jeton);
        listesActionsArmees.put(armee, listeActionsArmee);
    }

    private ActionTextutelle<UUID> actionSauvegarde(UUID uuidFichier, String nom, NVGPolice police) {
        ActionTextutelle<UUID> action = new ActionTextutelle<>(uuidFichier, 48f, police, BLANC, () -> nom);
        actionsPartiesCharger.ajouter(uuidFichier, action);
        return action;
    }

    private void construireMenu() throws LwjglException, JeuException {
        NVGPolice police = AffichageTeteHaute.obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT);
        ActionTextutelle<Void> titre = new ActionTextutelle<>(64f, police, BLANC, () -> "Guerres puniques");
        ActionTextutelle<Void> charger = new ActionTextutelle<>(BOUTON_TAILLE_POLICE, police, BLANC, () -> "Charger la partie");
        nouvellePartie = new ActionTextutelle<>(BOUTON_TAILLE_POLICE, police, BLANC, () -> "Nouvelle partie");
        sauvegarder = new ActionTextutelle<>(BOUTON_TAILLE_POLICE, police, BLANC, () -> "Sauvegarder la partie");
        quitter = new ActionTextutelle<>(BOUTON_TAILLE_POLICE, police, BLANC, () -> "Quitter");
        menu = new Menu(fenetre, Collections.singleton(titre), Arrays.asList(nouvellePartie, charger, sauvegarder, quitter));
        menu.ajouterPage(charger,
                jeu.registre()
                        .getInformations()
                        .entrySet()
                        .stream()
                        .map(element -> actionSauvegarde(element.getKey(), element.getValue().getNom(), police))
                        .toList(),
                police);
    }

    private void construireJeu() {
        lancerDes = new ActionImagee<>(100, 100, true, AffichageTeteHaute.obtenirImage(fenetre.contexteNvg(), IMAGE_LANCER_DEX));
        finirTour = new ActionImagee<>(100, 100, true, AffichageTeteHaute.obtenirImage(fenetre.contexteNvg(), IMAGE_FINIR_LE_TOUR));

        jeu.souscription((ChangementPartie) nouvelle -> {
            try {
                partie = nouvelle;
                fenetre.affichages().clear();
                fenetre.enfants().clear();
                construireInterfaceTeteHaute();
                construirePlateauJeu();
                menu.premierePage();
                barreActionsGenerale.afficher(fenetre.uuid());
                barresActionsCivilisations.forEach((civilisation, barreActions) -> barreActions.afficher(civilisation.getNom()));
                fenetre.apparaitre();
            } catch (LwjglException e) {
                LOGGER.error("Impossible de construire le jeu", e);
            }
        });
        jeu.souscription((ChangementDesCivilisation) _ -> informationsDesCivilisation.afficher());
        jeu.souscription((ChangementDesActions) _ -> informationDesActions.afficher());
        jeu.souscription((ChangementDeploiementArmee) (armee, region) -> {
            try {
                placer(armee, region);
                jetonsArmees.identifiable(armee).activer();
            } catch (LwjglException e) {
                LOGGER.error("Impossible de déployer une armée", e);
            }
        });
        jeu.souscription((ChangementDeplacementArmee) (armee, region) -> cadrillage.deplacer(jetonsArmees.identifiable(armee), region.ligne(), region.colonne()));
        jeu.souscription((ChangementSelectionArmee) armee -> {
            Forme forme = jetonsArmees.identifiable(armee);
            ActionImagee<CivilisationArmee> actionImagee = actionsArmees.identifiable(armee);
            barreActionsGenerale.desactiverActions();
            barreActionsGenerale.afficher(actionImagee.uuid());
            barresActionsCivilisations.forEach((_, barreActions) -> barreActions.desactiverActions());
            cadrillage.desactiverJetons();
            if (forme != null) {
                forme.activer();
            }
            actionImagee.activer();
        });
        jeu.souscription((ChangementDeselectionArmee) () -> {
            barreActionsGenerale.desactiverActions();
            barreActionsGenerale.afficher(fenetre.uuid());
            barresActionsCivilisations.values().forEach(BarreActions::desactiverActions);
            cadrillage.desactiverJetons();
        });
        jeu.souscription((ChangementAttaqueArmee) (attaquant, defenseur) -> System.out.println(MessageFormat.format("{0} attaque {1}", attaquant, defenseur)));
        jeu.souscription((ChangementAttaqueCivilisation) (armee, civilisation) -> System.out.println(MessageFormat.format("{0} attaque {1}", armee, civilisation)));
        jeu.souscription((ChangementFinTour) () -> System.out.println("Fin de tour"));

        fenetre.souscrire(_ -> jeu.deselectionner());
        fenetre.souscrire(nouvellePartie, _ -> jeu.nouvellePartie(tailleCadrillage));
        fenetre.souscrire(sauvegarder, _ -> jeu.sauvegarder());
        fenetre.souscrire(quitter, _ -> fenetre.fermer());
        fenetre.souscrire("Chargements", actionsPartiesCharger.identifiables, action -> jeu.demarrerPartie(action.objet()));
        fenetre.souscrire(lancerDes, _ -> jeu.lancerDes());
        fenetre.souscrire(finirTour, _ -> jeu.finirTour());
        fenetre.souscrire("Civilisations", actionsCivilisations.identifiables,
                _ -> System.out.println("Civilisation"),
                actionImagee -> jeu.attaquer(actionImagee.objet()));
        fenetre.souscrire("Armées", actionsArmees.identifiables, actionImagee -> jeu.amorcer(actionImagee.objet().armee));
        fenetre.souscrire("Ajouts d'unités", actionsArmeesAjouterUnite.identifiables, actionImagee -> {
            CivilisationArmeeTypeUnite objet = actionImagee.objet();
            jeu.rattacher(objet.civilisation, objet.armee, objet.typeUnite);
        });
        fenetre.souscrire("Déploiements de armée", actionsArmeesDeployer.identifiables, actionImagee -> {
            CivilisationArmee objet = actionImagee.objet();
            jeu.deployerArmee(objet.civilisation, objet.armee);
        });
        fenetre.souscrire("Attaques d'armée", actionsArmeesAttaquer.identifiables, action -> jeu.attaquer(action.objet()));
        fenetre.souscrire("Jetons", jetonsArmees.identifiables,
                forme -> jeu.amorcer(jetonsArmees.objet(forme)),
                forme -> listesActionsArmees.get(jetonsArmees.objet(forme)).placer(fenetre.projection(), fenetre.homogeneite(), forme).apparaitre());
        fenetre.souscrire("Cases", cases.identifiables, forme -> {
            Integer[] index = cases.objet(forme);
            jeu.deplacer(index[0], index[1]);
        });
    }

    private void construireInterfaceTeteHaute() throws LwjglException {
        int ordre = 0;
        for (Civilisation civilisation : partie.getCivilisations()) {
            construireImages(civilisation);
            construireBarreActions(civilisation, ordre++);
        }
        construireBarreActions();
        configurerBarresActions();
        construireDesCivilisation();
        construireDesActions();
    }

    private void construireImages(Civilisation civilisation) {
        AffichageTeteHaute.creerImage(fenetre.contexteNvg(), identifiantImageCite(civilisation), cheminImageCite(civilisation));
        civilisation.getTypesUnites().forEach(type -> AffichageTeteHaute.creerImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_UNITES, type), cheminImageType(civilisation, Jeu.NOM_GROUPE_TYPES_UNITES, type)));
        civilisation.getTypeArmees().forEach(type -> AffichageTeteHaute.creerImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_ARMEES, type), cheminImageType(civilisation, Jeu.NOM_GROUPE_TYPES_ARMEES, type)));
        civilisation.getTypesTransports().forEach(type -> AffichageTeteHaute.creerImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_TRANSPORTS, type), cheminImageType(civilisation, Jeu.NOM_GROUPE_TYPES_TRANSPORTS, type)));
        AffichageTeteHaute.creerImage(fenetre.contexteNvg(), identifiantImageDeploiementArmee(civilisation), cheminImageDeploiementArmee(civilisation));
    }

    private void construirePlateauJeu() throws LwjglException {
        Path cheminMonde = dossierTextures.resolve("monde");
        cadrillage = new Cadrillage(fenetre,
                1,
                tailleCadrillage,
                tailleJetons,
                new float[]{-tailleCadrillage[1] / 2f, -tailleCadrillage[0] / 2f, PROFONDEUR},
                Map.of(Pseudo.PSEUDO_BASE, cheminMonde.resolve("monde.png")));
        cases.ajouter(cadrillage.casesParIndex());

        for (Region region : jeu.fluxRegionsOccuper()) {
            for (Armee armee : region.getArmees()) {
                placer(armee, region);
            }
        }
    }

    private void construireBarreActions() throws LwjglException {
        barreActionsGenerale = new BarreActions<>(fenetre,
                new Disposition(Orientation.HORIZONTAL,
                        Justification.DEBUT,
                        Alignement.DEBUT,
                        Dimensionnement.FIXE,
                        20,
                        0,
                        fenetre.largeur() - 240),
                Position.HAUT,
                120,
                fenetre.hauteur() - 110,
                10);

        partie.getCivilisations().forEach(civilisation -> civilisation.getArmees().forEach(armee -> {
            civilisation.getTypesUnites().forEach(type -> {
                ActionImagee<CivilisationArmeeTypeUnite> actionImageeArmeeAjouterUnite = new ActionImagee<>(
                        new CivilisationArmeeTypeUnite(civilisation, armee, type),
                        100,
                        100,
                        false,
                        AffichageTeteHaute.obtenirImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_UNITES, type)));
                actionsArmeesAjouterUnite.ajouter(armee, type, actionImageeArmeeAjouterUnite);
            });

            ActionImagee<CivilisationArmee> actionImageeDeployerArmee = new ActionImagee<>(
                    new CivilisationArmee(civilisation, armee),
                    100,
                    100,
                    false,
                    AffichageTeteHaute.obtenirImage(fenetre.contexteNvg(), identifiantImageDeploiementArmee(civilisation)));
            actionsArmeesDeployer.ajouter(armee, actionImageeDeployerArmee);
        }));
    }

    private void construireBarreActions(Civilisation civilisation, int ordre) throws LwjglException {
        BarreActions<String> barreActionsCivilisation = new BarreActions<>(fenetre,
                new Disposition(Orientation.VERTICAL,
                        Justification.DEBUT,
                        Alignement.CENTRAL,
                        Dimensionnement.FIXE,
                        30,
                        0,
                        fenetre.hauteur() - 130),
                ordre == 0 ? Position.DROITE : Position.GAUCHE,
                ordre == 0 ? 10 : fenetre.largeur() - 110,
                10,
                4);

        ActionImagee<Civilisation> actionImageeCivilisation = new ActionImagee<>(civilisation, 100, 100, false, AffichageTeteHaute.obtenirImage(fenetre.contexteNvg(), identifiantImageCite(civilisation)));
        actionsCivilisations.ajouter(civilisation, actionImageeCivilisation);

        civilisation.getArmees().forEach(armee -> {
            ActionImagee<CivilisationArmee> actionImageeArmee = new ActionImagee<>(
                    new CivilisationArmee(civilisation, armee),
                    50,
                    50,
                    true,
                    AffichageTeteHaute.obtenirImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_ARMEES, armee.getType())));
            actionsArmees.ajouter(armee, actionImageeArmee);
        });

        barresActionsCivilisations.put(civilisation, barreActionsCivilisation);
    }

    private void configurerBarresActions() {
        barreActionsGenerale.ajouter(fenetre.uuid(), lancerDes, finirTour);

        NVGPolice police = AffichageTeteHaute.obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT);
        partie.getCivilisations().forEach(civilisation -> {
            BarreActions<String> barreActionsCivilisation = barresActionsCivilisations.get(civilisation);
            ActionImagee<Civilisation> actionImageeCivilisation = actionsCivilisations.identifiable(civilisation);
            barreActionsCivilisation.ajouter(actionImageeCivilisation);
            barreActionsCivilisation.ajouter(actionImageeCivilisation.uuid(), creerInformations(police, civilisation::getNom));
            barreActionsCivilisation.ajouter(actionImageeCivilisation.uuid(), informationType(police, civilisation.getTypesUnites(), civilisation.getReserve().getUnites()));
            barreActionsCivilisation.ajouter(actionImageeCivilisation.uuid(), informationType(police, civilisation.getTypesTransports(), civilisation.getTransports()));

            civilisation.getArmees().forEach(armee -> {
                ActionImagee<CivilisationArmee> actionImageeArmee = actionsArmees.identifiable(armee);
                civilisation.getTypesUnites().forEach(type -> {
                    ActionImagee<CivilisationArmeeTypeUnite> actionImageeArmeeAjouterUnite = actionsArmeesAjouterUnite.identifiable(armee, type);
                    barreActionsGenerale.ajouter(actionImageeArmee.uuid(), actionImageeArmeeAjouterUnite);
                    formatterInformations(barreActionsGenerale, actionImageeArmeeAjouterUnite, police, () -> MessageFormat.format("Ajouter l''unité {0}", type.getNom()));
                    formatterInformations(barreActionsGenerale, actionImageeArmeeAjouterUnite, police, () -> MessageFormat.format("Constitution : {0}", type.getConstitution()));
                    formatterInformations(barreActionsGenerale, actionImageeArmeeAjouterUnite, police, () -> MessageFormat.format("Force : {0}", type.getForce()));
                    actionImageeArmee.lier(actionImageeArmeeAjouterUnite);
                    actionImageeCivilisation.lier(actionImageeArmeeAjouterUnite);

                    formatterInformations(barreActionsCivilisation,
                            actionImageeArmee,
                            police,
                            () -> MessageFormat.format("{0}: {1}", type.getLibelle(), armee.getUnites().stream().filter(unite -> unite.getType() == type).count()));
                });
                ActionImagee<CivilisationArmee> actionImageeDeployerArmee = actionsArmeesDeployer.identifiable(armee);
                barreActionsGenerale.ajouter(actionImageeArmee.uuid(), actionImageeDeployerArmee);
                barreActionsGenerale.ajouter(actionImageeDeployerArmee.uuid(), creerInformations(police, () -> "Deployer l'armée"));

                barreActionsCivilisation.ajouter(civilisation.getNom(), actionImageeArmee);
                formatterInformations(barreActionsCivilisation, actionImageeArmee, police, () -> MessageFormat.format("Force : {0}", armee.getUnites().stream().mapToLong(unite -> unite.getType().getForce()).sum()));
            });
        });
    }

    private ActionTextutelle<Void> informationType(NVGPolice police, Collection<? extends Type> types, Collection<? extends Typable> typables) {
        return creerInformations(police, () -> types.stream().map(type -> MessageFormat.format("{0} ({1}/{2})",
                        type.getLibelle(),
                        typables.stream().filter(unite -> unite.getType() == type).count(),
                        type.getMaximum()))
                .collect(Collectors.joining(", ")));
    }

    private void construireDesCivilisation() throws LwjglException {
        informationsDesCivilisation = new Informations(fenetre, 10, fenetre.hauteur() - 110, 100, 100);
        informationsDesCivilisation.ajouter(creerValeurDes(AffichageTeteHaute.obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT), () -> partie.getDesCivilisation().getValeur() != null ? partie.getDesCivilisation().getValeur().toString() : ""));
        informationsDesCivilisation.afficher();
    }

    private void construireDesActions() throws LwjglException {
        informationDesActions = new Informations(fenetre, fenetre.largeur() - 110, fenetre.hauteur() - 110, 100, 100);
        informationDesActions.ajouter(creerValeurDes(AffichageTeteHaute.obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT), () -> partie.getDesActions().getValeur() != null ? partie.getDesActions().getValeur().toString() : ""));
        informationDesActions.afficher();
    }

    private String identifiantImageCite(Civilisation civilisation) {
        return String.join(":", IMAGE_CIVILISATION_PREFIX, civilisation.getIdentifiant().toString());
    }

    private <T extends Type> String identifiantImageType(Civilisation civilisation, String groupe, T type) {
        return String.join(":", groupe, civilisation.getIdentifiant().toString(), type.getIdentifiant().toString());
    }

    private String identifiantImageDeploiementArmee(Civilisation civilisation) {
        return String.join(":", IMAGE_DEPLOIEMENT_ARMEE_PREFIX, civilisation.getIdentifiant().toString());
    }

    private Path cheminImageCite(Civilisation civilisation) {
        return dossierTextures.resolve(civilisation.getNom().toLowerCase(), "cite.png");
    }

    private <T extends Type> Path cheminImageType(Civilisation civilisation, String groupe, T type) {
        return dossierTextures.resolve(civilisation.getNom().toLowerCase(), groupe, type.getNom().toLowerCase() + ".png");
    }

    private Path cheminImageDeploiementArmee(Civilisation civilisation) {
        return dossierTextures.resolve(civilisation.getNom().toLowerCase(), "armees", "deployer.png");
    }

    public static class Identificateur<T, I extends Identifiable> {
        private final List<I> identifiables;
        private final Map<T, I> identifiablesParObjets;
        private final Map<I, T> objetsParIdentifiables;

        public Identificateur() {
            this.identifiables = new LinkedList<>();
            this.identifiablesParObjets = new HashMap<>();
            this.objetsParIdentifiables = new HashMap<>();
        }

        public void ajouter(T objet, I identifiable) {
            identifiables.add(identifiable);
            identifiablesParObjets.put(objet, identifiable);
            objetsParIdentifiables.put(identifiable, objet);
        }

        public void ajouter(Map<T, I> tableauAssociatif) {
            tableauAssociatif.forEach(this::ajouter);
        }

        public I identifiable(T objet) {
            return identifiablesParObjets.get(objet);
        }

        public T objet(I identifiable) {
            return objetsParIdentifiables.get(identifiable);
        }
    }

    public static class BiIdentificateur<T, U, I extends Identifiable> {
        private final List<I> identifiables;
        private final Map<T, Map<U, I>> identifiablesParObjets;

        public BiIdentificateur() {
            this.identifiables = new LinkedList<>();
            this.identifiablesParObjets = new HashMap<>();
        }

        public void ajouter(T objet1, U objet2, I identifiable) {
            identifiables.add(identifiable);
            identifiablesParObjets.computeIfAbsent(objet1, _ -> new HashMap<>()).put(objet2, identifiable);
        }

        public I identifiable(T objet1, U objet2) {
            return identifiablesParObjets.getOrDefault(objet1, Collections.emptyMap()).get(objet2);
        }
    }

    private record CivilisationArmeeTypeUnite(Civilisation civilisation, Armee armee, TypeUnite typeUnite) {
    }

    private record CivilisationArmee(Civilisation civilisation, Armee armee) {
    }
}