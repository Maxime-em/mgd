package org.mgd.lwjgl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
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

    protected final Fenetre fenetre;

    protected Application(String titre, int hauteur, int ratioNumerateur, int ratioDenominateur) throws LwjglException {
        this.fenetre = new Fenetre(titre, hauteur, ratioNumerateur, ratioDenominateur);
        DetecteurService.obtenir().souscrire(cle -> GLFWErrorCallback.createPrint(System.err));
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
        long accumulator = 0;
        long previous = System.currentTimeMillis();
        while (!fenetre.fermeture()) {
            long current = System.currentTimeMillis();
            accumulator += (current - previous) * UPS;
            previous = current;

            while (accumulator >= 1_000) {
                maj();
                accumulator -= 1_000;
            }

            produire(accumulator);

            fenetre.inverser();

            long elapsed;
            do {
                elapsed = System.currentTimeMillis() - current;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                }
            } while (elapsed * FPS - 1_000 < 0);

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

    private void maj() {
        //System.out.println("Update " + " : " + glfwGetTime());
        fenetre.maj();
    }

    private void produire(long v) {
        //System.out.println("Render " + " : " + glfwGetTime() + " : " + v);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Ombreur.utiliser();
        fenetre.produire();
        Ombreur.perdre();
    }
}
