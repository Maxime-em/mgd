package org.mgd.guerres.puniques;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.guerres.puniques.coeur.objet.Civilisation;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.jeu.Jeu;
import org.mgd.guerres.puniques.jeu.exception.JeuException;
import org.mgd.lwjgl.Application;
import org.mgd.lwjgl.ITisseur;
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
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class GuerresPuniquesApplication extends Application {
    public static final String APPLICATION_CONFIGURATION = "application.configuration";
    private static final Logger LOGGER = LogManager.getLogger(GuerresPuniquesApplication.class);
    private static final float PROFONDEUR = -20f;
    private static final float[] BOUTON_MARGES = new float[]{0f, 0f, 30f, 0f};
    private static final float BOUTON_TAILLE_POLICE = 48f;
    private static final String BOUTON_NOUVELLE_PARTIE = "Créer et démarrer une nouvelle partie";
    private static final String BOUTON_CHARGER_PARTIE = "Charger une partie";
    private static final String BOUTON_DEMPARRER_PARTIE = "Démarrer une partie";
    private static final String BOUTON_QUITTER = "Quitter le jeu";

    private static final Path dossierTextures = Path.of("E:\\IdeaProjects\\mgd\\guerres-puniques-lwjgl\\src\\main\\resources\\textures");
    private static final Path dossierIconesActions = Path.of("E:\\IdeaProjects\\mgd\\guerres-puniques-lwjgl\\src\\main\\resources\\actions");

    private final Jeu jeu;
    private final Map<String, AffichageTeteHaute.NVGImage> iconesCivilisations = new HashMap<>();

    private AffichageTeteHaute.NVGPolice police;

    private Cadrillage cadrillage;
    private Jeton jeton;

    private Menu menu;
    private Informations informations;

    protected GuerresPuniquesApplication() throws LwjglException, IOException {
        super("Guerres puniques", 1280, 16, 9);
        Path configuration = Paths.get(System.getProperty(GuerresPuniquesApplication.APPLICATION_CONFIGURATION, "./"));
        Path fichier = Files.isRegularFile(configuration) ? configuration : configuration.resolve("configuration.properties");
        try (BufferedReader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            Properties proprietes = new Properties();
            proprietes.load(lecteur);
            this.jeu = new Jeu(proprietes);
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
            construireMenu().apparaitre();

            jeu.souscription(partie -> {
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
        AffichageTeteHaute.Ecrit titre = new AffichageTeteHaute.Ecrit(64f, police, AffichageTeteHaute.BLANC, "Guerres puniques", new float[]{150f, 0f, 200f, 0f});
        AffichageTeteHaute.Bouton<Void> boutonNouvellePartie = new AffichageTeteHaute.Bouton<>(BOUTON_NOUVELLE_PARTIE, BOUTON_TAILLE_POLICE, police, AffichageTeteHaute.BLANC, "Nouvelle partie", BOUTON_MARGES, null);
        AffichageTeteHaute.Bouton<Void> boutonCharger = new AffichageTeteHaute.Bouton<>(BOUTON_CHARGER_PARTIE, BOUTON_TAILLE_POLICE, police, AffichageTeteHaute.BLANC, "Charger partie", BOUTON_MARGES, null);
        AffichageTeteHaute.Bouton<Void> boutonQuitter = new AffichageTeteHaute.Bouton<>(BOUTON_QUITTER, BOUTON_TAILLE_POLICE, police, AffichageTeteHaute.BLANC, "Quitter", BOUTON_MARGES, null);
        menu = new Menu(fenetre, Collections.singleton(titre), Arrays.asList(boutonNouvellePartie, boutonCharger, boutonQuitter));
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
                                    element.getValue().getNom(),
                                    new float[]{0f, 0f, 30f, 0f},
                                    element.getKey());
                            bouton.souscrire(jeu::demarrerPartie);
                            return bouton;
                        })
                        .toList(),
                police);
        boutonNouvellePartie.souscrire(objet -> jeu.nouvellePartie());
        boutonQuitter.souscrire(objet -> fenetre.fermer());
        return menu;
    }

    private void construireInterfaceTeteHaute(Partie partie) throws LwjglException {
        informations = new Informations(fenetre, 48f, police, AffichageTeteHaute.BLANC, new float[]{20f, 0f, 20f, 0f});

        construireBarreActions();
        int ordre = 0;
        for (Civilisation civilisation : partie.getCivilisations()) {
            construireBarreActions(civilisation, ordre++);
        }
    }

    private void construirePlateauJeu() throws LwjglException {
        /*jeton = new Jeton(fenetre, "jeton", 0.5f, 0.5f, 0f, 0f, new float[]{-Jeu.NOMBRE_COLONNES / 2f - 1.5f, Jeu.NOMBRE_LIGNES / 2f - 1f, PROFONDEUR}, Map.of(
                ITisseur.PSEUDO_BASE, dossierTextures.resolve("armee_romaine.png")
        ));
        jeton.souscrire(objet -> informations.afficher(jeton, "Jeton"));
        jeton.souscrire(() -> informations.effacer(jeton));*/

        cadrillage = new Cadrillage(fenetre, "carte", Jeu.NOMBRE_COLONNES, Jeu.NOMBRE_LIGNES, PROFONDEUR, Map.of(
                ITisseur.PSEUDO_BASE, dossierTextures.resolve("carte.png"),
                ITisseur.PSEUDO_SURVOLE, dossierTextures.resolve("carte_survoler.png"),
                ITisseur.PSEUDO_ACTIVER, dossierTextures.resolve("carte_activer.png")
        ));
        cadrillage.souscrire(index -> informations.afficher(cadrillage, MessageFormat.format("index {0}", Arrays.toString(index))));
        cadrillage.souscrire(() -> informations.effacer(cadrillage));
    }

    private void construireBarreActions() throws LwjglException {
        AffichageTeteHaute.Action actionDeplacer = new AffichageTeteHaute.Action(0, 100, 100, true, true, fenetre.creerImage("Déplacer", dossierIconesActions.resolve("generales").resolve("deplacer.png")));
        AffichageTeteHaute.Action actionJouerNouveauTour = new AffichageTeteHaute.Action(1, 100, 100, true, true, fenetre.creerImage("Jouer nouveau tour", dossierIconesActions.resolve("generales").resolve("jouer_nouveau_tour.png")));
        new BarreActions(fenetre,
                Arrays.asList(actionDeplacer, actionJouerNouveauTour),
                10,
                BarreActions.Orientation.HORIZONTAL,
                BarreActions.Justification.CENTRAL,
                BarreActions.Ajustement.DEBUT,
                0,
                120,
                fenetre.hauteur() - 110,
                fenetre.largeur() - 240);
        actionDeplacer.souscrire(index -> cadrillage.accuellir(jeton, 0, 0));
    }

    private void construireBarreActions(Civilisation civilisation, int ordre) throws LwjglException {
        AffichageTeteHaute.NVGImage nvgImageCivilisation = iconesCivilisations.computeIfAbsent(civilisation.getNom(), nom -> fenetre.creerImage("Sélectionner " + nom, dossierIconesActions.resolve("civilisations").resolve(nom.toLowerCase() + ".png")));
        AffichageTeteHaute.Action actionCivilisation = new AffichageTeteHaute.Action(0, 100, 100, false, true, nvgImageCivilisation);

        AtomicInteger indexArmee = new AtomicInteger(1);
        List<AffichageTeteHaute.Action> actionsArmees = civilisation.getArmees().stream().map(armee -> {
            AffichageTeteHaute.NVGImage nvgImageArmee = iconesCivilisations.computeIfAbsent(armee.getType().name().toLowerCase(), nom -> fenetre.creerImage("Sélectionner une armée de type" + nom, dossierIconesActions.resolve("armees").resolve(nom + ".png")));
            return new AffichageTeteHaute.Action(indexArmee.getAndIncrement(), 50, 50, true, false, nvgImageArmee);
        }).toList();
        List<AffichageTeteHaute.Action> actions = Stream.concat(Stream.of(actionCivilisation), actionsArmees.stream()).toList();

        BarreActions barreActions = new BarreActions(fenetre,
                actions,
                civilisation.getArmees().size() + 1,
                BarreActions.Orientation.VERTICAL,
                BarreActions.Justification.DEBUT,
                BarreActions.Ajustement.CENTRAL,
                30,
                ordre == 0 ? 10 : fenetre.largeur() - 110,
                10,
                fenetre.hauteur() - 130);
        barreActions.souscrire(() -> informations.effacer(barreActions));
        actionCivilisation.souscrire(index -> {
            try {
                informations.effacer();
                informations.afficher(barreActions, civilisation.getInformations());
            } catch (IllegalArgumentException e) {
                informations.effacer(barreActions);
            }
        });
        actionsArmees.forEach(action -> action.souscrire(index -> informations.afficher(barreActions, Objects.requireNonNull(civilisation.getArmees().get(index - 1)).getInformations())));
    }
}