package org.mgd.lwjgl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.interne.GestionnaireOmbrages;
import org.mgd.lwjgl.souscription.GestionnaireEvenementsGlfw;

import java.nio.file.Path;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.*;
import static org.mgd.lwjgl.Programme.NOM_OMBRAGE_PAR_DEFAUT;

public abstract class Application {
    private static final Logger LOGGER = LogManager.getLogger(Application.class);
    private static final int UPS = 120;
    private static final int FPS = 60;
    private static final long NANOS_PAR_SECONDE = 1_000_000_000L;
    private static final long DUREE_MAXIMALE_IMAGE_NS = 250_000_000L;

    protected final Fenetre fenetre;
    private boolean capacitesOpenGlCreees;

    protected Application(String titre, int hauteur, int ratioNumerateur, int ratioDenominateur) throws LwjglException {
        LOGGER.info("Inscription au service de détection d'erreurs.");
        GestionnaireEvenementsGlfw.obtenir().souscrire((erreur, description) -> LOGGER.error("Erreur {} lors de l''éxecution de LWJGL - {}.", erreur, description));
        LOGGER.info("Construction de la fenêtre.");
        this.fenetre = new Fenetre(titre, hauteur, ratioNumerateur, ratioDenominateur);
    }

    protected void configurer(Path dossier) {
        LOGGER.info("Initialisation du programme d'ombrage par défaut depuis {}.", dossier);
        Programme.nouveau(NOM_OMBRAGE_PAR_DEFAUT, EtatVisuel.PSEUDO_BASE, dossier, new String[]{"vecteur", "fragment"});
    }

    protected abstract void peupler() throws LwjglException;

    public void demarrer() throws LwjglException {
        LOGGER.info("{} : LWJGL !", Version.getVersion());
        try {
            initialiser();
            boucler();
        } catch (LwjglException e) {
            LOGGER.error("Erreur lors de l'exécution de l'application LWJGL.", e);
            throw e;
        } finally {
            liberer();
        }
    }

    private void initialiser() throws LwjglException {
        LOGGER.info("Initialisation d'OpenGL.");
        GL.createCapabilities();
        capacitesOpenGlCreees = true;

        LOGGER.info("Création des programmes d'ombrages.");
        for (Programme programme : GestionnaireOmbrages.programmes()) {
            GestionnaireOmbrages.creer(programme);
        }

        LOGGER.info("Peuplement de la fenêtre.");
        peupler();

        LOGGER.info("Affichage de la fenêtre.");
        fenetre.montrer();
    }

    private void boucler() throws LwjglException {
        long accumulateurMaj = 0;
        long accumulateurRendu = 0;
        long reliquatMillisecondes = 0;
        long tempsPrecedent = System.nanoTime();
        long tempsTotal = 0;
        while (!fenetre.fermeture()) {
            long tempsCourant = System.nanoTime();
            long dureeImage = Math.min(tempsCourant - tempsPrecedent, DUREE_MAXIMALE_IMAGE_NS);
            tempsPrecedent = tempsCourant;
            tempsTotal += dureeImage;

            accumulateurMaj += dureeImage * UPS;
            while (accumulateurMaj >= NANOS_PAR_SECONDE) {
                reliquatMillisecondes += 1_000;
                maj(reliquatMillisecondes / UPS);
                reliquatMillisecondes %= UPS;
                accumulateurMaj -= NANOS_PAR_SECONDE;
            }

            accumulateurRendu += dureeImage * FPS;
            if (accumulateurRendu >= NANOS_PAR_SECONDE) {
                produire(tempsTotal / 1_000_000);
                fenetre.inverser();
                accumulateurRendu %= NANOS_PAR_SECONDE;
            }

            glfwPollEvents();
        }
    }

    private void liberer() {
        try {
            if (capacitesOpenGlCreees) {
                GestionnaireOmbrages.nettoyer();
            }
        } finally {
            try {
                fenetre.liberer(capacitesOpenGlCreees);
            } finally {
                LOGGER.info("Fermeture de l'application LWJGL.");
            }
        }
    }

    private void maj(long accumulateur) throws LwjglException {
        fenetre.maj(accumulateur);
    }

    private void produire(long tempsEcouleMs) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        fenetre.produire(tempsEcouleMs);
    }
}
