package org.mgd.lwjgl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.interne.Ombreur;
import org.mgd.lwjgl.souscription.DetecteurService;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.*;
import static org.mgd.lwjgl.Programme.NOM_PAR_DEFAUT;

public abstract class Application {
    private static final Logger LOGGER = LogManager.getLogger(Application.class);
    private static final int UPS = 120;
    private static final int FPS = 60;
    private static final int MSPU = 1_000 / UPS;
    private static final int MSPF = 1_000 / FPS;

    protected final Fenetre fenetre;

    protected Application(String titre, int hauteur, int ratioNumerateur, int ratioDenominateur) throws LwjglException {
        try {
            this.fenetre = new Fenetre(titre, hauteur, ratioNumerateur, ratioDenominateur);
            Programme.nouveau(
                    NOM_PAR_DEFAUT,
                    Pseudo.DEFAUT,
                    Path.of(Objects.requireNonNull(Ombreur.class.getResource("")).toURI()),
                    new String[]{"vecteur", "fragment"},
                    new String[]{"projection", "vision", "transformation", "echantillonneur"});
            DetecteurService.obtenir().souscrire((erreur, description) -> LOGGER.error("Erreur {} lors de l''éxecution de LWJGL - {}", erreur, description));
        } catch (URISyntaxException e) {
            throw new LwjglException(e);
        }
    }


    protected abstract void peupler() throws LwjglException;

    public void demarrer() throws LwjglException {
        LOGGER.info("{} : LWJGL !", Version.getVersion());

        initialiser();
        boucler();
        liberer();
    }

    private void initialiser() throws LwjglException {
        GL.createCapabilities();

        for (Programme programme : Ombreur.programmes()) {
            Ombreur.creer(programme);
        }

        peupler();
        fenetre.montrer();
    }

    private void boucler() throws LwjglException {
        /*
         * Run the rendering loop until the user has attempted to close
         * the window or has pressed the ESCAPE key.
         */
        long courant;
        long accumulateur = 0;
        long ellipse = 0;
        long precedent = System.currentTimeMillis();
        while (!fenetre.fermeture()) {
            courant = System.currentTimeMillis();

            accumulateur += courant - precedent;
            while (accumulateur >= MSPU) {
                maj(accumulateur);
                accumulateur -= MSPU;
            }

            ellipse += courant - precedent;
            while (ellipse >= MSPF) {
                produire(ellipse);
                ellipse -= MSPF;
            }

            precedent = courant;

            fenetre.inverser();

            /*
             * Poll for window events. The key callback above will only be
             * invoked during this call.
             */
            glfwPollEvents();
        }
    }

    private void liberer() {
        Ombreur.nettoyer();
        DetecteurService.obtenir().liberer();

        fenetre.liberer();
    }

    private void maj(long accumulateur) throws LwjglException {
        fenetre.maj(accumulateur);
    }

    private void produire(long ellipse) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        fenetre.produire(ellipse);
    }
}
