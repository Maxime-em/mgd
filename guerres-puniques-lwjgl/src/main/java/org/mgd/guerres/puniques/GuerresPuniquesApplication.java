package org.mgd.guerres.puniques;

import org.mgd.connexion.exception.ConnexionException;
import org.mgd.guerres.puniques.jeu.Jeu;
import org.mgd.lwjgl.*;
import org.mgd.lwjgl.element.*;
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
import java.util.Map;
import java.util.Properties;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class GuerresPuniquesApplication extends Application {
    public static final String APPLICATION_CONFIGURATION = "application.configuration";

    public static final String IDENTIFIANT_BOUTON_NOUVELLE_PARTIE = "Nouvelle partie";
    public static final String IDENTIFIANT_BOUTON_CHARGER_PARTIE = "Charger partie";

    private final Jeu jeu;

    protected GuerresPuniquesApplication() throws LwjglException, IOException, ConnexionException {
        super("Guerres puniques", 1280, 16, 9);
        Path configuration = Paths.get(System.getProperty(GuerresPuniquesApplication.APPLICATION_CONFIGURATION, "./"));
        Path fichier = Files.isRegularFile(configuration) ? configuration : configuration.resolve("configuration.properties");
        try (BufferedReader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            Properties proprietes = new Properties();
            proprietes.load(lecteur);
            this.jeu = new Jeu(proprietes);
        }
    }

    public static void main(String[] args) throws LwjglException, IOException, URISyntaxException, ConnexionException {
        new GuerresPuniquesApplication().demarrer();
    }

    @Override
    protected void peupler(Fenetre fenetre) throws LwjglException {
        fenetre.creerContexteNvg();
        AffichageTeteHaute.NVGPolice calibri = fenetre.creerPolice("Calibri", Path.of("C:\\Windows\\Fonts\\Calibri.ttf"));

        AffichageTeteHaute.Ecrit titre = new AffichageTeteHaute.Ecrit(64f, calibri, AffichageTeteHaute.BLANC, "Guerres puniques", new float[]{150f, 0f, 200f, 0f});
        AffichageTeteHaute.Bouton boutonNouvellePartie = new AffichageTeteHaute.Bouton(IDENTIFIANT_BOUTON_NOUVELLE_PARTIE, 48f, calibri, AffichageTeteHaute.BLANC, "Nouvelle partie", new float[]{0f, 0f, 30f, 0f});
        AffichageTeteHaute.Bouton boutonCharger = new AffichageTeteHaute.Bouton(IDENTIFIANT_BOUTON_CHARGER_PARTIE, 48f, calibri, AffichageTeteHaute.BLANC, "Charger partie", new float[]{0f, 0f, 30f, 0f});
        Menu menu = new Menu(fenetre);
        menu.ajouteTitre(titre);
        menu.ajouteBouton(boutonNouvellePartie);
        menu.ajouteBouton(boutonCharger);
        menu.apparaitre();
        menu.souscrire((parent, identifiant) -> {
            switch (identifiant) {
                case IDENTIFIANT_BOUTON_NOUVELLE_PARTIE:
                    menu.disparaitre();
                    fenetre.enfants().forEach(Primitif::apparaitre);
                    fenetre.affichages().forEach(Primitif::apparaitre);
                    break;

                case IDENTIFIANT_BOUTON_CHARGER_PARTIE:
                    // TODO
                    break;
            }
        });

        Informations informations = new Informations(fenetre, 48f, calibri, AffichageTeteHaute.BLANC, new float[]{0f, 0f, 0f, 0f});

        int replicationAbscisse = 32;
        int replicationOrdonnee = 18;
        float z = -20f;

        Jeton jeton = new Jeton(fenetre, "jeton", 0.5f, 0.5f, 0f, 0f, new float[]{-replicationAbscisse / 2f - 1.5f, replicationOrdonnee / 2f - 1f, z}, Map.of(
                ITisseur.PSEUDO_BASE, "E:\\IdeaProjects\\mgd\\guerres-puniques-lwjgl\\src\\main\\resources\\jetons\\armee_romaine.png"
        ));
        jeton.souscrire((forme, objet) -> informations.afficher(jeton, "Jeton"));
        jeton.souscrire(objet -> informations.effacer(jeton));

        Cadrillage cadrillage = new Cadrillage(fenetre, "carte", replicationAbscisse, replicationOrdonnee, z, Map.of(
                ITisseur.PSEUDO_BASE, "E:\\IdeaProjects\\mgd\\guerres-puniques-lwjgl\\src\\main\\resources\\carte.png",
                ITisseur.PSEUDO_SURVOLE, "E:\\IdeaProjects\\mgd\\guerres-puniques-lwjgl\\src\\main\\resources\\carte_survoler.png",
                ITisseur.PSEUDO_ACTIVER, "E:\\IdeaProjects\\mgd\\guerres-puniques-lwjgl\\src\\main\\resources\\carte_activer.png"
        ));
        cadrillage.souscrire((forme, index) -> informations.afficher(cadrillage,
                MessageFormat.format("""
                                forme {0}
                                index {1}
                        """, forme, index)));
        cadrillage.souscrire(objet -> informations.effacer(cadrillage));

        AffichageTeteHaute.NVGImage iconesActions = fenetre.creerImage("Icônes des actions", Path.of("E:\\IdeaProjects\\mgd\\guerres-puniques-lwjgl\\src\\main\\resources\\actions\\icones.png"));
        Actions actions = new Actions(fenetre, 10, 100, 100, new int[]{0, 400, 30, 400}, iconesActions);
        actions.souscrire((image, index) -> {
            System.out.println("Action sous " + index);
            if (index == 0) {
                cadrillage.accuellir(jeton, 0, 0);
            }
        });

        DetecteurService.obtenir().souscrire(fenetre, (cle, code, action, modifications) -> {
            if (cle == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                menu.basculer();
                fenetre.enfants().forEach(Primitif::basculer);
                fenetre.affichages().forEach(Primitif::basculer);
            }
        });
    }
}