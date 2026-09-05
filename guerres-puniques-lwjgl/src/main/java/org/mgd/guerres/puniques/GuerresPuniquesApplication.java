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
import org.mgd.lwjgl.affichage.tetehaute.composant.*;
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

import static org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute.*;

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
    private static final String IMAGE_DEPLOIEMENT_TRANSPORT_PREFIX = "Déploiement d'un transport";
    private static final String POLICE_DEFAUT = "Calibri";
    private static final float PROFONDEUR = -21.5f;
    private static final float BOUTON_TAILLE_POLICE = 48f;

    private final Jeu jeu;
    private final Path dossierTextures;
    private final int[] tailleCadrillage;
    private final int[] tailleJetons;
    private final Identificateur<UUID, Action<UUID>> actionsPartiesCharger;
    private final Identificateur<Civilisation, Action<Civilisation>> actionsCivilisations;
    private final Identificateur<Armee, Action<CivilisationArmee>> actionsArmees;
    private final BiIdentificateur<Armee, TypeUnite, Action<CivilisationArmeeTypeUnite>> actionsArmeesAjouterUnite;
    private final Identificateur<TypeArmee, Action<CivilisationTypeArmee>> actionsArmeesDeployer;
    private final Identificateur<Armee, Action<Armee>> actionsArmeesAttaquer;
    private final Identificateur<Transport, Action<CivilisationTransport>> actionsTransports;
    private final Identificateur<TypeTransport, Action<CivilisationTypeTransport>> actionsTransportDeployer;
    private final Identificateur<Transport, Action<Transport>> actionsTransportsAttaquer;
    private final Identificateur<Transport, Action<Transport>> actionsTransportsEmbarquer;
    private final Identificateur<Armee, Forme> jetonsArmees;
    private final Identificateur<Transport, Forme> jetonsTransports;
    private final Identificateur<Integer[], Forme> cases;
    private final Map<Civilisation, Barre<String>> barresCivilisations;
    private final Map<Armee, Ephemere> listesActionsArmees;
    private final Map<Transport, Ephemere> listesActionsTransports;
    private Action<Void> lancerDes;
    private Action<Void> finirTour;
    private Action<Void> nouvellePartie;
    private Action<Void> sauvegarder;
    private Action<Void> quitter;
    private Cadrillage cadrillage;
    private Menu menu;
    private Barre<UUID> barreGenerale;
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
                Programme.nouveau(selecteur[0], selecteur[1], dossierRacine, description[1].split(":"));
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
            this.actionsTransports = new Identificateur<>();
            this.actionsTransportDeployer = new Identificateur<>();
            this.actionsTransportsAttaquer = new Identificateur<>();
            this.actionsTransportsEmbarquer = new Identificateur<>();
            this.jetonsArmees = new Identificateur<>();
            this.jetonsTransports = new Identificateur<>();
            this.cases = new Identificateur<>();
            this.barresCivilisations = new HashMap<>();
            this.listesActionsArmees = new HashMap<>();
            this.listesActionsTransports = new HashMap<>();
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

    private static Entite creerInformations(NVGPolice police, Supplier<String> texte) {
        return new ActionTextutelle<>(24f, police, BLANC, texte);
    }

    private static Entite creerValeurDes(NVGPolice police, Supplier<String> texte) {
        return new ActionTextutelle<>(48f, police, BLANC, texte);
    }

    @Override
    protected void peupler() throws LwjglException {
        fenetre.creerContexteNvg();
        creerPolice(fenetre.contexteNvg(), POLICE_DEFAUT, Path.of("C:\\Windows\\Fonts\\Calibri.ttf"));
        creerImage(fenetre.contexteNvg(), IMAGE_LANCER_DEX, dossierTextures.resolve("generales", "des.png"));
        creerImage(fenetre.contexteNvg(), IMAGE_FINIR_LE_TOUR, dossierTextures.resolve("generales", "fin_de_tour.png"));

        try {
            construireMenu();
            construireJeu();
            menu.apparaitre();
        } catch (JeuException e) {
            throw new LwjglException(e);
        }
    }

    private void ajouterActions(Armee armee) throws LwjglException {
        Civilisation civilisation = armee.getOrigine();
        Barre<String> barreCivilisation = barresCivilisations.get(civilisation);
        NVGPolice police = obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT);

        Action<CivilisationArmee> actionArmee = new ActionImagee<>(
                new CivilisationArmee(civilisation, armee),
                50,
                50,
                obtenirImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_ARMEES, armee.getType())));
        actionsArmees.ajouter(armee, actionArmee);
        barreCivilisation.ajouter(civilisation.getNom(), actionArmee);
        barreCivilisation.informer(actionArmee.uuid(), creerInformations(police, () -> armee.getType().getNom()));
        barreCivilisation.informer(actionArmee.uuid(), creerInformations(police, () -> MessageFormat.format("Force : {0}", armee.getUnites().stream().mapToLong(unite -> unite.getType().getForce()).sum())));

        Action<Civilisation> actionCivilisation = actionsCivilisations.identifiable(civilisation);
        civilisation.getTypesUnites().forEach(type -> {
            Action<CivilisationArmeeTypeUnite> actionArmeeAjouterUnite = new ActionImagee<>(
                    new CivilisationArmeeTypeUnite(civilisation, armee, type),
                    100,
                    100,
                    obtenirImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_UNITES, type)));
            actionsArmeesAjouterUnite.ajouter(armee, type, actionArmeeAjouterUnite);
            barreGenerale.ajouter(actionArmee.uuid(), actionArmeeAjouterUnite);
            barreGenerale.informer(actionArmeeAjouterUnite.uuid(), creerInformations(police, () -> MessageFormat.format("Ajouter l''unité {0}", type.getNom())));
            barreGenerale.informer(actionArmeeAjouterUnite.uuid(), creerInformations(police, () -> MessageFormat.format("Constitution : {0}", type.getConstitution())));
            barreGenerale.informer(actionArmeeAjouterUnite.uuid(), creerInformations(police, () -> MessageFormat.format("Force : {0}", type.getForce())));

            barreCivilisation.informer(actionArmee.uuid(), creerInformations(police, () -> MessageFormat.format("{0}: {1}", type.getLibelle(), armee.getUnites().stream().filter(unite -> unite.getType() == type).count())));

            actionArmee.lier(actionArmeeAjouterUnite);
            actionCivilisation.lier(actionArmeeAjouterUnite);
        });

        Action<Armee> actionAttaqueArmee = new ActionTextutelle<>(armee, 24f, police, BLANC, () -> "Attaquer");
        actionsArmeesAttaquer.ajouter(armee, actionAttaqueArmee);

        listesActionsArmees.put(armee, new Ephemere(fenetre, 10, 10, actionAttaqueArmee));
    }

    private void ajouterActions(Transport transport) throws LwjglException {
        Civilisation civilisation = transport.getOrigine();
        Barre<String> barreCivilisation = barresCivilisations.get(civilisation);
        NVGPolice police = obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT);

        Action<CivilisationTransport> actionTransport = new ActionImagee<>(
                new CivilisationTransport(civilisation, transport),
                50,
                50,
                obtenirImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_TRANSPORTS, transport.getType())));
        actionsTransports.ajouter(transport, actionTransport);
        barreCivilisation.ajouter(civilisation.getNom(), actionTransport);
        barreCivilisation.informer(actionTransport.uuid(), creerInformations(police, () -> transport.getType().getNom()));
        barreCivilisation.informer(actionTransport.uuid(), creerInformations(police, () -> MessageFormat.format("Armée à bord : {0}", transport.getArmees().size())));

        Action<Transport> actionTransportAttaquer = new ActionTextutelle<>(transport, 24f, police, BLANC, () -> "Attaquer");
        actionsTransportsAttaquer.ajouter(transport, actionTransportAttaquer);

        Action<Transport> actionTransportEmbarquer = new ActionTextutelle<>(transport, 24f, police, BLANC, () -> "Embarquer");
        actionsTransportsEmbarquer.ajouter(transport, actionTransportEmbarquer);

        listesActionsTransports.put(transport, new Ephemere(fenetre, 10, 10, actionTransportAttaquer, actionTransportEmbarquer));
    }

    private Entite actionSauvegarde(UUID uuidFichier, String nom, NVGPolice police) {
        Action<UUID> action = new ActionTextutelle<>(uuidFichier, 48f, police, BLANC, () -> nom);
        actionsPartiesCharger.ajouter(uuidFichier, action);
        return action;
    }

    private void construireMenu() throws LwjglException, JeuException {
        NVGPolice police = obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT);
        Action<Void> titre = new ActionTextutelle<>(64f, police, BLANC, () -> "Guerres puniques");
        Action<Void> charger = new ActionTextutelle<>(BOUTON_TAILLE_POLICE, police, BLANC, () -> "Charger la partie");
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
        lancerDes = new ActionImagee<>(100, 100, obtenirImage(fenetre.contexteNvg(), IMAGE_LANCER_DEX));
        finirTour = new ActionImagee<>(100, 100, obtenirImage(fenetre.contexteNvg(), IMAGE_FINIR_LE_TOUR));

        jeu.souscription((ChangementPartie) nouvelle -> {
            try {
                partie = nouvelle;
                fenetre.affichages().clear();
                fenetre.enfants().clear();
                construireInterfaceTeteHaute();
                construirePlateauJeu();
                menu.premierePage();
                barreGenerale.afficher(fenetre.uuid());
                barresCivilisations.forEach((civilisation, barre) -> barre.afficher(civilisation.getNom()));
                fenetre.apparaitre();
            } catch (LwjglException e) {
                LOGGER.error("Impossible de construire le jeu", e);
            }
        });
        jeu.souscription((ChangementSelectionCivilisation) civilisation -> {
            Action<Civilisation> action = actionsCivilisations.identifiable(civilisation);
            barreGenerale.desactiverEntites();
            barreGenerale.afficher(action.uuid());
            barresCivilisations.values().forEach(Barre::desactiverEntites);
            cadrillage.desactiverJetons();
        });
        jeu.souscription((ChangementDesCivilisation) _ -> informationsDesCivilisation.afficher());
        jeu.souscription((ChangementDesActions) _ -> informationDesActions.afficher());
        jeu.souscription(this::deployer);
        jeu.souscription((ChangementDeplacement) this::deplacer);
        jeu.souscription((ChangementSelection) this::selectionner);
        jeu.souscription((ChangementDeselection) () -> {
            barreGenerale.desactiverEntites();
            barreGenerale.afficher(fenetre.uuid());
            barresCivilisations.values().forEach(Barre::desactiverEntites);
            cadrillage.desactiverJetons();
        });
        jeu.souscription((ChangementAttaque) this::attaquer);
        jeu.souscription(this::embarquer);
        jeu.souscription((ChangementAttaqueCivilisation) this::attaquer);
        jeu.souscription((ChangementFinTour) () -> System.out.println("Fin de tour"));

        fenetre.souscrire(_ -> jeu.deselectionner());
        fenetre.souscrire(nouvellePartie, _ -> jeu.nouvellePartie(tailleCadrillage));
        fenetre.souscrire(sauvegarder, _ -> jeu.sauvegarder());
        fenetre.souscrire(quitter, _ -> fenetre.fermer());
        fenetre.souscrire("Chargements", actionsPartiesCharger.identifiables, action -> jeu.demarrerPartie(action.objet()));
        fenetre.souscrire(lancerDes, _ -> jeu.lancerDes());
        fenetre.souscrire(finirTour, _ -> jeu.finirTour());
        fenetre.souscrire("Civilisations", actionsCivilisations.identifiables,
                action -> jeu.amorcer(action.objet()),
                action -> jeu.attaquer(action.objet()));
        fenetre.souscrire("Armées", actionsArmees.identifiables, action -> jeu.amorcer(action.objet().armee));
        fenetre.souscrire("Ajouts d'unités", actionsArmeesAjouterUnite.identifiables, action -> {
            CivilisationArmeeTypeUnite objet = action.objet();
            jeu.rattacher(objet.civilisation, objet.armee, objet.typeUnite);
        });
        fenetre.souscrire("Déploiements de armée", actionsArmeesDeployer.identifiables, action -> {
            CivilisationTypeArmee objet = action.objet();
            jeu.deployer(objet.civilisation, objet.type);
        });
        fenetre.souscrire("Déploiements de transport", actionsTransportDeployer.identifiables, action -> {
            CivilisationTypeTransport objet = action.objet();
            jeu.deployer(objet.civilisation, objet.type);
        });
        fenetre.souscrire("Attaques d'armée", actionsArmeesAttaquer.identifiables, action -> jeu.attaquer(action.objet()));
        fenetre.souscrire("Jetons armées", jetonsArmees.identifiables,
                forme -> jeu.amorcer(jetonsArmees.objet(forme)),
                forme -> listesActionsArmees.get(jetonsArmees.objet(forme)).placer(fenetre.projection(), fenetre.homogeneite(), forme).apparaitre());
        fenetre.souscrire("Transports", actionsTransports.identifiables, action -> jeu.amorcer(action.objet().transport));
        fenetre.souscrire("Attaques de transport", actionsTransportsAttaquer.identifiables, action -> jeu.attaquer(action.objet()));
        fenetre.souscrire("Embarquer dans un transport", actionsTransportsEmbarquer.identifiables, action -> jeu.embarquer(action.objet()));
        fenetre.souscrire("Jetons transports", jetonsTransports.identifiables,
                forme -> jeu.amorcer(jetonsTransports.objet(forme)),
                forme -> listesActionsTransports.get(jetonsTransports.objet(forme)).placer(fenetre.projection(), fenetre.homogeneite(), forme).apparaitre());
        fenetre.souscrire("Cases", cases.identifiables, forme -> {
            Integer[] index = cases.objet(forme);
            jeu.deplacer(index[0], index[1]);
        });
    }

    private <T extends Type> Forme obtenirJeton(Tangible<T> objet) {
        return switch (objet.getType()) {
            case TypeArmee _ -> jetonsArmees.identifiable((Armee) objet);
            case TypeTransport _ -> jetonsTransports.identifiable((Transport) objet);
            default ->
                    throw new IllegalStateException(MessageFormat.format("Le jeton du type {0} ne peut pas être obtenu", objet.getType()));
        };
    }

    @SuppressWarnings("unchecked")
    private <T extends Type, U> Action<U> obtenirIdentifiable(Tangible<T> objet) {
        return (Action<U>) switch (objet.getType()) {
            case TypeArmee _ -> actionsArmees.identifiable((Armee) objet);
            case TypeTransport _ -> actionsTransports.identifiable((Transport) objet);
            default ->
                    throw new IllegalStateException(MessageFormat.format("L''action du type {0} ne peut pas être obtenue", objet.getType()));
        };
    }

    private <T extends Type> void placer(Tangible<T> objet, Region region) throws LwjglException {
        Forme jeton = cadrillage.ajouterJeton(region.ligne(), region.colonne(), objet.getType().ligne(), objet.getType().colonne());
        switch (objet.getType()) {
            case TypeArmee _ -> {
                Armee armee = (Armee) objet;
                ajouterActions(armee);
                actionsArmees.identifiable(armee).lier(jeton);
                listesActionsArmees.get(armee).lier(jeton);
                jetonsArmees.ajouter(armee, jeton);
            }
            case TypeTransport _ -> {
                Transport transport = (Transport) objet;
                ajouterActions(transport);
                actionsTransports.identifiable(transport).lier(jeton);
                listesActionsTransports.get(transport).lier(jeton);
                jetonsTransports.ajouter(transport, jeton);
                for (Armee armee : transport.getArmees()) {
                    placer(armee, region);
                    embarquer(armee, transport);
                }
            }
            default -> {
                // Rien à faire
            }
        }
    }

    private void embarquer(Armee armee, Transport transport) {
        Action<CivilisationArmee> actionArmee = actionsArmees.identifiable(armee);
        Action<CivilisationTransport> actionTransport = actionsTransports.identifiable(transport);
        Barre<String> barreCivilisation = barresCivilisations.get(armee.getOrigine());
        barreCivilisation.hierarchiser(armee.getOrigine().getNom(), actionTransport, actionArmee);

        jetonsArmees.identifiable(armee).disparaitre();
    }

    private <T extends Type> void deployer(Civilisation civilisation, Tangible<T> objet, Region region) {
        try {
            placer(objet, region);
            jeu.amorcer(objet);
            barresCivilisations.get(civilisation).afficher(civilisation.getNom(), true);
        } catch (LwjglException e) {
            LOGGER.error("Impossible de déployer une armée", e);
        }
    }

    private <T extends Type> void deplacer(Tangible<T> objet, Region region) {
        cadrillage.deplacer(obtenirJeton(objet), region.ligne(), region.colonne());
    }

    private <T extends Type> void selectionner(Tangible<T> objet) {
        Action<?> action = obtenirIdentifiable(objet);
        barreGenerale.desactiverEntites();
        barreGenerale.afficher(action.uuid());
        barresCivilisations.values().forEach(Barre::desactiverEntites);
        cadrillage.desactiverJetons();
        Forme forme = obtenirJeton(objet);
        if (forme != null) {
            forme.activer();
        }
        action.activer();
        barresCivilisations.get(objet.getOrigine()).afficher(objet.getOrigine().getNom(), true);
    }

    private <T extends Type, U extends Type> void attaquer(Tangible<T> attaquant, Tangible<U> defenseur) {
        System.out.println(MessageFormat.format("{0} attaque {1}", attaquant, defenseur));
    }

    private <T extends Type> void attaquer(Tangible<T> armee, Civilisation civilisation) {
        System.out.println(MessageFormat.format("{0} attaque {1}", armee, civilisation));
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
        creerImage(fenetre.contexteNvg(), identifiantImageCite(civilisation), cheminImageCite(civilisation));
        civilisation.getTypesUnites().forEach(type -> creerImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_UNITES, type), cheminImageType(civilisation, Jeu.NOM_GROUPE_TYPES_UNITES, type)));
        civilisation.getTypeArmees().forEach(type -> creerImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_ARMEES, type), cheminImageType(civilisation, Jeu.NOM_GROUPE_TYPES_ARMEES, type)));
        civilisation.getTypesTransports().forEach(type -> creerImage(fenetre.contexteNvg(), identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_TRANSPORTS, type), cheminImageType(civilisation, Jeu.NOM_GROUPE_TYPES_TRANSPORTS, type)));
        creerImage(fenetre.contexteNvg(), identifiantImageDeploiementArmee(civilisation), cheminImageDeploiementArmee(civilisation));
        creerImage(fenetre.contexteNvg(), identifiantImageDeploiementTransport(civilisation), cheminImageDeploiementTransport(civilisation));
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

        for (Region region : jeu.regionsOccuper()) {
            for (Armee armee : region.getArmees()) {
                placer(armee, region);
            }
            for (Transport transport : region.getTransports()) {
                placer(transport, region);
            }
        }
    }

    private void construireBarreActions() throws LwjglException {
        barreGenerale = new Barre<>(fenetre,
                new Disposition(Orientation.HORIZONTAL,
                        Justification.DEBUT,
                        Alignement.DEBUT,
                        Dimensionnement.FIXE,
                        20,
                        0,
                        fenetre.largeur() - 240),
                new Options(nouveauBoutonSuivante(), nouveauBoutonPrecedente(), Position.HAUT),
                120,
                fenetre.hauteur() - 110,
                10);
    }

    private Entite nouveauBoutonSuivante() {
        return new ActionTextutelle<>(24f, obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT), BLANC, () -> "Suivant");
    }

    private Entite nouveauBoutonPrecedente() {
        return new ActionTextutelle<>(24f, obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT), BLANC, () -> "Précedent");
    }

    private Entite nouveauBoutonSecondaire() {
        return new Fond(25, 50);
    }

    private void construireBarreActions(Civilisation civilisation, int ordre) throws LwjglException {
        Position position = ordre == 0 ? Position.DROITE : Position.GAUCHE;
        Barre<String> barreCivilisation = new Barre<>(fenetre,
                new Disposition(Orientation.VERTICAL,
                        Justification.DEBUT,
                        Alignement.CENTRAL,
                        Dimensionnement.FIXE,
                        30,
                        0,
                        fenetre.hauteur() - 130),
                new Options(nouveauBoutonSuivante(), nouveauBoutonPrecedente(), this::nouveauBoutonSecondaire, true, position, position),
                ordre == 0 ? 10 : fenetre.largeur() - 110,
                10,
                7);
        barresCivilisations.put(civilisation, barreCivilisation);

        Action<Civilisation> actionCivilisation = new ActionImagee<>(civilisation, 100, 100, obtenirImage(fenetre.contexteNvg(), identifiantImageCite(civilisation)));
        actionsCivilisations.ajouter(civilisation, actionCivilisation);

        civilisation.getTypeArmees().forEach(type -> {
            Action<CivilisationTypeArmee> actionArmeeDeployer = new ActionImagee<>(
                    new CivilisationTypeArmee(civilisation, type),
                    100,
                    100,
                    obtenirImage(fenetre.contexteNvg(), identifiantImageDeploiementArmee(civilisation)));
            actionsArmeesDeployer.ajouter(type, actionArmeeDeployer);
        });

        civilisation.getTypesTransports().forEach(type -> {
            Action<CivilisationTypeTransport> actionTransportDeployer = new ActionImagee<>(
                    new CivilisationTypeTransport(civilisation, type),
                    100,
                    100,
                    obtenirImage(fenetre.contexteNvg(), identifiantImageDeploiementTransport(civilisation)));
            actionsTransportDeployer.ajouter(type, actionTransportDeployer);
        });
    }

    private void configurerBarresActions() {
        barreGenerale.ajouter(fenetre.uuid(), lancerDes, finirTour);

        NVGPolice police = obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT);
        partie.getCivilisations().forEach(civilisation -> {
            Barre<String> barreCivilisation = barresCivilisations.get(civilisation);
            Action<Civilisation> actionCivilisation = actionsCivilisations.identifiable(civilisation);
            barreCivilisation.ajouter(actionCivilisation);
            barreCivilisation.informer(actionCivilisation.uuid(), creerInformations(police, civilisation::getNom));
            barreCivilisation.informer(actionCivilisation.uuid(), informationType(police, civilisation.getTypesUnites(), civilisation.getReserve().getUnites()));
            barreCivilisation.informer(actionCivilisation.uuid(), informationType(police, civilisation.getTypesTransports(), civilisation.getTransports()));
            barreCivilisation.ajouter(civilisation.getNom());

            civilisation.getTypeArmees().forEach(type -> {
                Action<CivilisationTypeArmee> actionArmeeDeployer = actionsArmeesDeployer.identifiable(type);
                barreGenerale.ajouter(actionCivilisation.uuid(), actionArmeeDeployer);
                barreGenerale.informer(actionArmeeDeployer.uuid(), creerInformations(police, () -> "Deployer une nouvelle armée"));
            });

            civilisation.getTypesTransports().forEach(type -> {
                Action<CivilisationTypeTransport> actionTransportDeployer = actionsTransportDeployer.identifiable(type);
                barreGenerale.ajouter(actionCivilisation.uuid(), actionTransportDeployer);
                barreGenerale.informer(actionTransportDeployer.uuid(), creerInformations(police, () -> "Deployer un nouveau transport"));
            });
        });
    }

    private <T extends Type, O extends Tangible<T>> Entite informationType(NVGPolice police, Collection<T> types, Collection<O> objets) {
        return creerInformations(police, () -> types.stream().map(type -> MessageFormat.format("{0} ({1}/{2})",
                        type.getLibelle(),
                        objets.stream().filter(objet -> objet.getType() == type).count(),
                        type.getMaximum()))
                .collect(Collectors.joining(", ")));
    }

    private void construireDesCivilisation() throws LwjglException {
        informationsDesCivilisation = new Informations(fenetre, 10, fenetre.hauteur() - 110, 100, 100);
        informationsDesCivilisation.ajouter(creerValeurDes(obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT), () -> partie.getDesCivilisation().getValeur() != null ? partie.getDesCivilisation().getValeur().toString() : ""));
        informationsDesCivilisation.afficher();
    }

    private void construireDesActions() throws LwjglException {
        informationDesActions = new Informations(fenetre, fenetre.largeur() - 110, fenetre.hauteur() - 110, 100, 100);
        informationDesActions.ajouter(creerValeurDes(obtenirPolice(fenetre.contexteNvg(), POLICE_DEFAUT), () -> partie.getDesActions().getValeur() != null ? partie.getDesActions().getValeur().toString() : ""));
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

    private String identifiantImageDeploiementTransport(Civilisation civilisation) {
        return String.join(":", IMAGE_DEPLOIEMENT_TRANSPORT_PREFIX, civilisation.getIdentifiant().toString());
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

    private Path cheminImageDeploiementTransport(Civilisation civilisation) {
        return dossierTextures.resolve(civilisation.getNom().toLowerCase(), "transports", "deployer.png");
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

    private record CivilisationTypeArmee(Civilisation civilisation, TypeArmee type) {
    }

    private record CivilisationTransport(Civilisation civilisation, Transport transport) {
    }

    private record CivilisationTypeTransport(Civilisation civilisation, TypeTransport type) {
    }
}