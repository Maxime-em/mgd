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
import org.mgd.lwjgl.affichage.tetehaute.BarreActions;
import org.mgd.lwjgl.affichage.tetehaute.Disposition;
import org.mgd.lwjgl.affichage.tetehaute.Informations;
import org.mgd.lwjgl.affichage.tetehaute.Menu;
import org.mgd.lwjgl.affichage.tetehaute.composant.Action;
import org.mgd.lwjgl.affichage.tetehaute.composant.Ecrit;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGPolice;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.DetecteurService;

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

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
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
    private final List<Action<Civilisation>> identifiablesCivilisations;
    private final List<Ecrit<UUID>> identifiablesChargementsParties;
    private final List<Action<CivilisationArmeeTypeUnite>> identifiablesArmeeAjouterUnite;
    private final List<Action<CivilisationArmee>> identifiablesArmeesDeployer;
    private final List<Action<CivilisationArmee>> identifiablesArmees;
    private final Map<Forme, Armee> identifiablesJeton;
    private final Map<Forme, Integer[]> identifiablesCase;
    private final Map<Armee, Forme> jetonsParArmee;
    private final Map<Civilisation, Action<Civilisation>> actionsCivilisations;
    private final Map<Armee, Map<TypeUnite, Action<CivilisationArmeeTypeUnite>>> actionsArmeeAjouterUnite;
    private final Map<Armee, Action<CivilisationArmee>> actionsArmeesDeployer;
    private final Map<Armee, Action<CivilisationArmee>> actionsArmees;
    private final Map<Civilisation, BarreActions<String>> barresActionsCivilisations;
    private Action<Void> lancerDes;
    private Action<Void> finirTour;
    private Ecrit<Void> nouvellePartie;
    private Ecrit<Void> sauvegarder;
    private Ecrit<Void> quitter;
    private Cadrillage cadrillage;
    private Menu menu;
    private BarreActions<UUID> barreActionsGenerale;
    private Informations informationsDesCivilisation;
    private Informations informationDesActions;
    private Partie partie;

    protected GuerresPuniquesApplication() throws LwjglException, IOException {
        super("Guerres puniques", 960, 16, 9);
        Path configuration = Paths.get(System.getProperty(APPLICATION_CONFIGURATION, "./"));
        Path fichier = Files.isRegularFile(configuration) ? configuration : configuration.resolve("configuration.properties");
        try (BufferedReader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            Properties proprietes = new Properties();
            proprietes.load(lecteur);
            this.jeu = new Jeu(proprietes);

            Path dossierRacine = Path.of(proprietes.getProperty(APPLICATION_RACINE));
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
            this.dossierTextures = dossierRacine.resolve("textures");
            this.tailleCadrillage = Arrays.stream(proprietes.getProperty(APPLICATION_TAILLE_CADRILLAGE).split(";")).mapToInt(Integer::parseInt).toArray();
            this.tailleJetons = Arrays.stream(proprietes.getProperty(APPLICATION_TAILLE_JETONS).split(";")).mapToInt(Integer::parseInt).toArray();
            this.identifiablesCivilisations = new ArrayList<>();
            this.identifiablesChargementsParties = new ArrayList<>();
            this.identifiablesArmeeAjouterUnite = new ArrayList<>();
            this.identifiablesArmeesDeployer = new ArrayList<>();
            this.identifiablesArmees = new ArrayList<>();
            this.identifiablesJeton = new HashMap<>();
            this.identifiablesCase = new HashMap<>();
            this.jetonsParArmee = new HashMap<>();
            this.actionsCivilisations = new HashMap<>();
            this.actionsArmees = new HashMap<>();
            this.barresActionsCivilisations = new HashMap<>();
            this.actionsArmeeAjouterUnite = new HashMap<>();
            this.actionsArmeesDeployer = new HashMap<>();
        } catch (JeuException e) {
            throw new LwjglException(e);
        }
    }

    static void main() throws LwjglException, IOException {
        new GuerresPuniquesApplication().demarrer();
    }

    private static Ecrit<Void> creerInformations(NVGPolice police, Supplier<String> texte) {
        return new Ecrit<>(24f, police, BLANC, texte);
    }

    public static <G, T> void formatterInformations(BarreActions<G> barreActions, Action<T> action, NVGPolice police, Supplier<String> texte) {
        barreActions.ajouter(action.uuid(), new Ecrit<>(24f, police, BLANC, texte));
    }

    private static Ecrit<Void> creerValeurDes(NVGPolice police, Supplier<String> texte) {
        return new Ecrit<>(48f, police, BLANC, texte);
    }

    @Override
    protected void peupler() throws LwjglException {
        fenetre.creerContexteNvg();
        fenetre.creerPolice(POLICE_DEFAUT, Path.of("C:\\Windows\\Fonts\\Calibri.ttf"));
        fenetre.creerImage(IMAGE_LANCER_DEX, dossierTextures.resolve("generales", "des.png"));
        fenetre.creerImage(IMAGE_FINIR_LE_TOUR, dossierTextures.resolve("generales", "fin_de_tour.png"));

        try {
            DetecteurService.obtenir().souscrire(fenetre, (cle, _, action, _) -> {
                if (cle == GLFW_KEY_ESCAPE && action == GLFW_PRESS && jeu.avecPartieEnCours()) {
                    fenetre.basculer();
                }
            });
            construireMenu();
            construireJeu();
            menu.apparaitre();
        } catch (JeuException e) {
            throw new LwjglException(e);
        }
    }

    private void placer(Armee armee, Region region) {
        Forme jeton = cadrillage.ajouterJeton(region.ligne(), region.colonne(), armee.getType().ligne(), armee.getType().colonne());
        identifiablesJeton.put(jeton, armee);
        jetonsParArmee.put(armee, jeton);
        actionsArmees.get(armee).lier(jeton);
    }

    private Ecrit<UUID> ecritSauvegarde(UUID uuidFichier, String nom, NVGPolice police) {
        Ecrit<UUID> bouton = new Ecrit<>(uuidFichier, 48f, police, BLANC, () -> nom);
        identifiablesChargementsParties.add(bouton);
        return bouton;
    }

    private void construireMenu() throws LwjglException, JeuException {
        NVGPolice police = fenetre.obtenirPolice(POLICE_DEFAUT);
        Ecrit<Void> titre = new Ecrit<>(64f, police, BLANC, () -> "Guerres puniques");
        Ecrit<Void> charger = new Ecrit<>(BOUTON_TAILLE_POLICE, police, BLANC, () -> "Charger la partie");
        nouvellePartie = new Ecrit<>(BOUTON_TAILLE_POLICE, police, BLANC, () -> "Nouvelle partie");
        sauvegarder = new Ecrit<>(BOUTON_TAILLE_POLICE, police, BLANC, () -> "Sauvegarder la partie");
        quitter = new Ecrit<>(BOUTON_TAILLE_POLICE, police, BLANC, () -> "Quitter");
        menu = new Menu(fenetre, Collections.singleton(titre), Arrays.asList(nouvellePartie, charger, sauvegarder, quitter));
        menu.ajouterPage(charger,
                jeu.registre()
                        .getInformations()
                        .entrySet()
                        .stream()
                        .map(element -> ecritSauvegarde(element.getKey(), element.getValue().getNom(), police))
                        .toList(),
                police);
    }

    private void construireJeu() {
        lancerDes = new Action<>(100, 100, true, fenetre.obtenirImage(IMAGE_LANCER_DEX));
        finirTour = new Action<>(100, 100, true, fenetre.obtenirImage(IMAGE_FINIR_LE_TOUR));

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
                fenetre.basculer();
            } catch (LwjglException e) {
                LOGGER.error("Impossible de construire le jeu", e);
            }
        });
        jeu.souscription((ChangementDesCivilisation) _ -> informationsDesCivilisation.afficher());
        jeu.souscription((ChangementDesActions) _ -> informationDesActions.afficher());
        jeu.souscription((ChangementDeploiementArmee) (armee, region) -> {
            placer(armee, region);
            jetonsParArmee.get(armee).activer();
        });
        jeu.souscription((ChangementDeplacementArmee) (armee, region) -> cadrillage.deplacer(jetonsParArmee.get(armee), region.ligne(), region.colonne()));
        jeu.souscription((ChangementSelectionArmee) armee -> {
            Forme forme = jetonsParArmee.get(armee);
            Action<CivilisationArmee> action = actionsArmees.get(armee);
            barreActionsGenerale.desactiverActions();
            barreActionsGenerale.afficher(action.uuid());
            barresActionsCivilisations.forEach((_, barreActions) -> barreActions.desactiverActions());
            cadrillage.desactiverJetons();
            if (forme != null) {
                forme.activer();
            }
            action.activer();
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
        fenetre.souscrire("Chargements", identifiablesChargementsParties, ecrit -> jeu.demarrerPartie(ecrit.objet()));
        fenetre.souscrire(lancerDes, _ -> jeu.lancerDes());
        fenetre.souscrire(finirTour, _ -> jeu.finirTour());
        fenetre.souscrire("Civilisations", identifiablesCivilisations,
                _ -> System.out.println("Civilisation"),
                action -> jeu.attaquer(action.objet()));
        fenetre.souscrire("Ajouts d'unités", identifiablesArmeeAjouterUnite, action -> {
            CivilisationArmeeTypeUnite objet = action.objet();
            jeu.rattacher(objet.civilisation, objet.armee, objet.typeUnite);
        });
        fenetre.souscrire("Armées", identifiablesArmees,
                action -> jeu.amorcer(action.objet().armee),
                action -> jeu.attaquer(action.objet().armee));
        fenetre.souscrire("Déploiements de armée", identifiablesArmeesDeployer, action -> {
            CivilisationArmee objet = action.objet();
            jeu.deployerArmee(objet.civilisation, objet.armee);
        });
        fenetre.souscrire("Jetons", identifiablesJeton.keySet(),
                forme -> jeu.amorcer(identifiablesJeton.get(forme)),
                forme -> jeu.attaquer(identifiablesJeton.get(forme)));
        fenetre.souscrire("Cases", identifiablesCase.keySet(), forme -> {
            Integer[] index = identifiablesCase.get(forme);
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
        fenetre.creerImage(identifiantImageCite(civilisation), cheminImageCite(civilisation));
        civilisation.getTypesUnites().forEach(type -> fenetre.creerImage(identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_UNITES, type), cheminImageType(civilisation, Jeu.NOM_GROUPE_TYPES_UNITES, type)));
        civilisation.getTypeArmees().forEach(type -> fenetre.creerImage(identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_ARMEES, type), cheminImageType(civilisation, Jeu.NOM_GROUPE_TYPES_ARMEES, type)));
        civilisation.getTypesTransports().forEach(type -> fenetre.creerImage(identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_TRANSPORTS, type), cheminImageType(civilisation, Jeu.NOM_GROUPE_TYPES_TRANSPORTS, type)));
        fenetre.creerImage(identifiantImageDeploiementArmee(civilisation), cheminImageDeploiementArmee(civilisation));
    }

    private void construirePlateauJeu() throws LwjglException {
        Path cheminMonde = dossierTextures.resolve("monde");
        cadrillage = new Cadrillage(fenetre,
                1,
                tailleCadrillage,
                tailleJetons,
                new float[]{-tailleCadrillage[1] / 2f, -tailleCadrillage[0] / 2f, PROFONDEUR},
                Map.of(Pseudo.PSEUDO_BASE, cheminMonde.resolve("monde.png")));
        identifiablesCase.putAll(cadrillage.indexParCase());
        jeu.fluxRegionsOccuper().forEach(region -> region.getArmees().forEach(armee -> placer(armee, region)));
    }

    private void construireBarreActions() throws LwjglException {
        barreActionsGenerale = new BarreActions<>(fenetre,
                new Disposition(Disposition.Orientation.HORIZONTAL,
                        Disposition.Justification.DEBUT,
                        Disposition.Alignement.DEBUT,
                        Disposition.Position.HAUT,
                        20),
                120,
                fenetre.hauteur() - 110,
                fenetre.largeur() - 240);

        partie.getCivilisations().forEach(civilisation -> civilisation.getArmees().forEach(armee -> {
            civilisation.getTypesUnites().forEach(type -> {
                Action<CivilisationArmeeTypeUnite> actionArmeeAjouterUnite = new Action<>(
                        new CivilisationArmeeTypeUnite(civilisation, armee, type),
                        100,
                        100,
                        false,
                        fenetre.obtenirImage(identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_UNITES, type)));
                identifiablesArmeeAjouterUnite.add(actionArmeeAjouterUnite);
                actionsArmeeAjouterUnite.computeIfAbsent(armee, _ -> new HashMap<>()).put(type, actionArmeeAjouterUnite);
            });

            Action<CivilisationArmee> actionDeployerArmee = new Action<>(
                    new CivilisationArmee(civilisation, armee),
                    100,
                    100,
                    false,
                    fenetre.obtenirImage(identifiantImageDeploiementArmee(civilisation)));
            identifiablesArmeesDeployer.add(actionDeployerArmee);
            actionsArmeesDeployer.put(armee, actionDeployerArmee);
        }));
    }

    private void construireBarreActions(Civilisation civilisation, int ordre) throws LwjglException {
        BarreActions<String> barreActionsCivilisation = new BarreActions<>(fenetre,
                new Disposition(Disposition.Orientation.VERTICAL,
                        Disposition.Justification.DEBUT,
                        Disposition.Alignement.CENTRAL,
                        ordre == 0 ? Disposition.Position.DROITE : Disposition.Position.GAUCHE,
                        30),
                ordre == 0 ? 10 : fenetre.largeur() - 110,
                10,
                fenetre.hauteur() - 130);

        Action<Civilisation> actionCivilisation = new Action<>(civilisation, 100, 100, false, fenetre.obtenirImage(identifiantImageCite(civilisation)));
        identifiablesCivilisations.add(actionCivilisation);
        actionsCivilisations.put(civilisation, actionCivilisation);

        civilisation.getArmees().forEach(armee -> {
            Action<CivilisationArmee> actionArmee = new Action<>(
                    new CivilisationArmee(civilisation, armee),
                    50,
                    50,
                    true,
                    fenetre.obtenirImage(identifiantImageType(civilisation, Jeu.NOM_GROUPE_TYPES_ARMEES, armee.getType())));
            identifiablesArmees.add(actionArmee);
            actionsArmees.put(armee, actionArmee);
        });

        barresActionsCivilisations.put(civilisation, barreActionsCivilisation);
    }

    private void configurerBarresActions() {
        barreActionsGenerale.ajouter(fenetre.uuid(), lancerDes, finirTour);

        NVGPolice police = fenetre.obtenirPolice(POLICE_DEFAUT);
        partie.getCivilisations().forEach(civilisation -> {
            BarreActions<String> barreActionsCivilisation = barresActionsCivilisations.get(civilisation);
            Action<Civilisation> actionCivilisation = actionsCivilisations.get(civilisation);
            barreActionsCivilisation.ajouter(civilisation.getNom(), actionCivilisation);
            barreActionsCivilisation.ajouter(actionCivilisation.uuid(), creerInformations(police, civilisation::getNom));
            barreActionsCivilisation.ajouter(actionCivilisation.uuid(), informationType(police, civilisation.getTypesUnites(), civilisation.getReserve().getUnites()));
            barreActionsCivilisation.ajouter(actionCivilisation.uuid(), informationType(police, civilisation.getTypesTransports(), civilisation.getTransports()));

            civilisation.getArmees().forEach(armee -> {
                Action<CivilisationArmee> actionArmee = actionsArmees.get(armee);
                civilisation.getTypesUnites().forEach(type -> {
                    Action<CivilisationArmeeTypeUnite> actionArmeeAjouterUnite = actionsArmeeAjouterUnite.getOrDefault(armee, Collections.emptyMap()).get(type);
                    barreActionsGenerale.ajouter(actionArmee.uuid(), actionArmeeAjouterUnite);
                    formatterInformations(barreActionsGenerale, actionArmeeAjouterUnite, police, () -> MessageFormat.format("Ajouter l''unité {0}", type.getNom()));
                    formatterInformations(barreActionsGenerale, actionArmeeAjouterUnite, police, () -> MessageFormat.format("Constitution : {0}", type.getConstitution()));
                    formatterInformations(barreActionsGenerale, actionArmeeAjouterUnite, police, () -> MessageFormat.format("Force : {0}", type.getForce()));
                    actionArmee.lier(actionArmeeAjouterUnite);
                    actionCivilisation.lier(actionArmeeAjouterUnite);

                    formatterInformations(barreActionsCivilisation,
                            actionArmee,
                            police,
                            () -> MessageFormat.format("{0}: {1}", type.getLibelle(), armee.getUnites().stream().filter(unite -> unite.getType() == type).count()));
                });
                Action<CivilisationArmee> actionDeployerArmee = actionsArmeesDeployer.get(armee);
                barreActionsGenerale.ajouter(actionArmee.uuid(), actionDeployerArmee);
                barreActionsGenerale.ajouter(actionDeployerArmee.uuid(), creerInformations(police, () -> "Deployer l'armée"));

                barreActionsCivilisation.ajouter(civilisation.getNom(), actionArmee);
                formatterInformations(barreActionsCivilisation, actionArmee, police, () -> MessageFormat.format("Force : {0}", armee.getUnites().stream().mapToLong(unite -> unite.getType().getForce()).sum()));
            });
        });
    }

    private Ecrit<Void> informationType(NVGPolice police, Collection<? extends Type> types, Collection<? extends Typable> typables) {
        return creerInformations(police, () -> types.stream().map(type -> MessageFormat.format("{0} ({1}/{2})",
                        type.getLibelle(),
                        typables.stream().filter(unite -> unite.getType() == type).count(),
                        type.getMaximum()))
                .collect(Collectors.joining(", ")));
    }

    private void construireDesCivilisation() throws LwjglException {
        informationsDesCivilisation = new Informations(fenetre, 10, fenetre.hauteur() - 110, 100, 100);
        informationsDesCivilisation.ajouter(creerValeurDes(fenetre.obtenirPolice(POLICE_DEFAUT), () -> partie.getDesCivilisation().getValeur() != null ? partie.getDesCivilisation().getValeur().toString() : ""));
        informationsDesCivilisation.afficher();
    }

    private void construireDesActions() throws LwjglException {
        informationDesActions = new Informations(fenetre, fenetre.largeur() - 110, fenetre.hauteur() - 110, 100, 100);
        informationDesActions.ajouter(creerValeurDes(fenetre.obtenirPolice(POLICE_DEFAUT), () -> partie.getDesActions().getValeur() != null ? partie.getDesActions().getValeur().toString() : ""));
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

    private record CivilisationArmeeTypeUnite(Civilisation civilisation, Armee armee, TypeUnite typeUnite) {
    }

    private record CivilisationArmee(Civilisation civilisation, Armee armee) {
    }
}