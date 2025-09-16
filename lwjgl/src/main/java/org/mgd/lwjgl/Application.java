package org.mgd.lwjgl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.mgd.lwjgl.affichage.element.Element;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.interne.Ombreur;
import org.mgd.lwjgl.souscription.DetecteurService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.*;

public abstract class Application {
    private static final Logger LOGGER = LogManager.getLogger(Application.class);
    private static final int UPS = 120;
    private static final int FPS = 60;
    private static final int MSPU = 1_000 / UPS;
    private static final int MSPF = 1_000 / FPS;

    protected final Fenetre fenetre;

    protected Application(String titre, int hauteur, int ratioNumerateur, int ratioDenominateur) throws LwjglException {
        this.fenetre = new Fenetre(titre, hauteur, ratioNumerateur, ratioDenominateur);
        DetecteurService.obtenir().souscrire((erreur, description) -> LOGGER.error("Erreur {} lors de l''éxecution de LWJGL - {}", erreur, description));
    }

    public void demarrer() throws LwjglException, IOException, URISyntaxException {
        LOGGER.info("{} : LWJGL !", Version.getVersion());

        initialiser();
        boucler();
        liberer();
    }

    private void initialiser() throws LwjglException, IOException, URISyntaxException {
        GL.createCapabilities();

        Ombreur.creer();
        Ombreur.compiler();
        Ombreur.charger();

        peupler();
        fenetre.enfants().sort(Comparator.comparing(Element::minimumz));
        fenetre.montrer();
    }

    protected abstract void peupler() throws LwjglException;

    private void boucler() {
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
        DetecteurService.obtenir().nettoyer();

        fenetre.liberer();
    }

    private void maj(long accumulateur) {
        fenetre.maj();
    }

    private void produire(long ellipse) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Ombreur.utiliser();
        fenetre.produire(ellipse);
        Ombreur.perdre();
    }
}
