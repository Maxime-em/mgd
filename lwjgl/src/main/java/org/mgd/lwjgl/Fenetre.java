package org.mgd.lwjgl;

import org.lwjgl.glfw.GLFWVidMode;
import org.mgd.commun.IConstantesMathematiques;
import org.mgd.commun.Matrice;
import org.mgd.lwjgl.element.Element;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.interne.DetecteurService;
import org.mgd.lwjgl.interne.Menu;
import org.mgd.lwjgl.interne.Ombreur;
import org.mgd.lwjgl.interne.Vision;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Fenetre {
    private static final int DEPLACEMENT_VISION_AUCUN = 0x00;
    private static final int DEPLACEMENT_VISION_HAUT = 0x01;
    private static final int DEPLACEMENT_VISION_DROITE = 0x02;
    private static final int DEPLACEMENT_VISION_BAS = 0x04;
    private static final int DEPLACEMENT_VISION_GAUCHE = 0x08;

    private static final int ZOOM_VISION_AUCUN = 0x00;
    private static final int ZOOM_VISION_AVANT = 0x01;
    private static final int ZOOM_VISION_ARRIERE = 0x02;

    private final long identifiant;
    private final int largeur;
    private final int hauteur;

    private final Vision vision;
    private final Menu menu;
    private final LinkedList<Element> enfants;
    private final int ratioNumerateur;
    private final int ratioDenominateur;
    private final Souris souris;
    private final Clavier clavier;

    public Fenetre(String titre) throws LwjglException {
        this.vision = new Vision();
        this.menu = new Menu();
        this.enfants = new LinkedList<>();
        this.souris = new Souris();
        this.clavier = new Clavier();

        /*
         * Initialisation de GLFW. La plupart des fonctions GLFW ne fonctionneront
         * pas avant cette opération.
         */
        if (!glfwInit()) throw new LwjglException("Impossible d'initialiser GLFW.");

        // Configuration de GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, 4);

        // Création d'une nouvelle fenêtre
        this.ratioNumerateur = 16;
        this.ratioDenominateur = 9;
        this.hauteur = 900;
        this.largeur = this.ratioNumerateur * 900 / this.ratioDenominateur;
        this.identifiant = glfwCreateWindow(this.largeur, this.hauteur, titre, NULL, NULL);
        if (this.identifiant == 0L) throw new LwjglException("Impossible de créer la fenêtre.");

        // Obtenir la résolution de l'écran et centrer la fenêtre
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode != null) {
            glfwSetWindowPos(this.identifiant, (vidmode.width() - this.largeur) / 2, (vidmode.height() - this.hauteur) / 2);
        }

        // Construire le contexte courant d'OpenGL
        glfwMakeContextCurrent(this.identifiant);

        // Activer la synchronisation verticale
        glfwSwapInterval(1);

        // Gestion des entrées-sorties
        DetecteurService.obtenir()
                .souscrire(this.identifiant, this.clavier::gerer)
                .souscrire(identifiant, this.souris::calculer)
                .souscrire(this.identifiant, this.souris::diriger)
                .souscrire(identifiant, this.souris::selectionner);
    }

    public void initialiser() throws LwjglException, IOException {
        menu.initialiser();
    }

    public void montrer() {
        glfwShowWindow(identifiant);
    }

    public boolean fermeture() {
        return glfwWindowShouldClose(identifiant);
    }

    public void inverser() {
        glfwSwapBuffers(identifiant);
    }

    public void maj() {
        switch (clavier.deplacement) {
            case DEPLACEMENT_VISION_HAUT -> vision.translater(0f, 0.01f, 0f);
            case DEPLACEMENT_VISION_HAUT | DEPLACEMENT_VISION_DROITE -> vision.translater(0.01f, 0.01f, 0f);
            case DEPLACEMENT_VISION_DROITE -> vision.translater(0.01f, 0f, 0f);
            case DEPLACEMENT_VISION_BAS | DEPLACEMENT_VISION_DROITE -> vision.translater(0.01f, -0.01f, 0f);
            case DEPLACEMENT_VISION_BAS -> vision.translater(0f, -0.01f, 0f);
            case DEPLACEMENT_VISION_BAS | DEPLACEMENT_VISION_GAUCHE -> vision.translater(-0.01f, -0.01f, 0f);
            case DEPLACEMENT_VISION_GAUCHE -> vision.translater(-0.01f, 0f, 0f);
            case DEPLACEMENT_VISION_HAUT | DEPLACEMENT_VISION_GAUCHE -> vision.translater(-0.01f, 0.01f, 0f);
        }

        switch (clavier.zoom) {
            case ZOOM_VISION_ARRIERE -> vision.translater(0f, 0f, 0.01f);
            case ZOOM_VISION_AVANT -> vision.translater(0f, 0f, -0.01f);
        }

        souris.placer(new float[]{vision.translationx(), vision.translationy(), vision.translationz()});
    }

    public void produire() {
        Ombreur.specifier("projection", Matrice.projection60());
        if (menu.visible()) {
            menu.produire(largeur, hauteur);
        } else {
            vision.produire();
            enfants.forEach(enfant -> enfant.produire(souris));
        }
    }

    public void liberer() {
        glfwDestroyWindow(identifiant);
        glfwFreeCallbacks(identifiant);

        menu.liberer();
        vision.liberer();
        enfants.forEach(Element::nettoyer);

        glfwTerminate();
    }

    public List<Element> enfants() {
        return enfants;
    }

    public int ratioNumerateur() {
        return ratioNumerateur;
    }

    public int ratioDenominateur() {
        return ratioDenominateur;
    }

    public class Souris {
        private final float[] coordonnees;
        private final float[] origine;
        private final float[] direction;
        private boolean calcul;
        private boolean selection;

        public Souris() {
            this.coordonnees = new float[2];
            this.origine = new float[3];
            this.direction = new float[3];
        }

        public float[] origine() {
            return origine;
        }

        public void placer(float[] origine) {
            System.arraycopy(origine, 0, this.origine, 0, 3);
        }

        public float[] coordonnees() {
            return coordonnees;
        }

        public float[] direction() {
            return direction;
        }

        public void diriger(double abscisse, double ordonnee) {
            coordonnees[0] = 2 * Double.valueOf(abscisse).floatValue() / largeur - 1;
            coordonnees[1] = 1 - 2 * Double.valueOf(ordonnee).floatValue() / hauteur;
            direction[0] = Double.valueOf(ratioNumerateur * (2 * abscisse - largeur) / (IConstantesMathematiques.RACINE_TROIS * largeur * ratioDenominateur)).floatValue();
            direction[1] = Double.valueOf((hauteur - 2 * ordonnee) / (IConstantesMathematiques.RACINE_TROIS * hauteur)).floatValue();
            direction[2] = -1f;
        }

        public boolean calcul() {
            return calcul;
        }

        public void calculer(boolean calcul) {
            this.calcul = calcul;
        }

        public boolean selection() {
            return selection;
        }

        public void selectionner(int bouton, int action, int mode) {
            this.selection = bouton == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS;
        }

        public void deselectionner() {
            this.selection = false;
        }
    }

    private class Clavier {
        private int deplacement;
        private int zoom;

        public Clavier() {
            this.deplacement = DEPLACEMENT_VISION_AUCUN;
            this.zoom = ZOOM_VISION_AUCUN;
        }

        public void gerer(int cle, int code, int action, int modifications) {
            if (cle == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                menu.basculer();
            } else {
                clavier.mouvoir(cle, action);
            }
        }

        private void mouvoir(int cle, int action) {
            if (action == GLFW_PRESS) {
                switch (cle) {
                    case GLFW_KEY_W:
                        deplacement |= DEPLACEMENT_VISION_HAUT;
                        break;

                    case GLFW_KEY_D:
                        deplacement |= DEPLACEMENT_VISION_DROITE;
                        break;

                    case GLFW_KEY_S:
                        deplacement |= DEPLACEMENT_VISION_BAS;
                        break;

                    case GLFW_KEY_A:
                        deplacement |= DEPLACEMENT_VISION_GAUCHE;
                        break;

                    case GLFW_KEY_Q:
                        zoom |= ZOOM_VISION_AVANT;
                        break;

                    case GLFW_KEY_E:
                        zoom |= ZOOM_VISION_ARRIERE;
                        break;
                }
            } else if (action == GLFW_RELEASE) {
                switch (cle) {
                    case GLFW_KEY_W:
                        deplacement ^= DEPLACEMENT_VISION_HAUT;
                        break;

                    case GLFW_KEY_D:
                        deplacement ^= DEPLACEMENT_VISION_DROITE;
                        break;

                    case GLFW_KEY_S:
                        deplacement ^= DEPLACEMENT_VISION_BAS;
                        break;

                    case GLFW_KEY_A:
                        deplacement ^= DEPLACEMENT_VISION_GAUCHE;
                        break;

                    case GLFW_KEY_Q:
                        zoom ^= ZOOM_VISION_AVANT;
                        break;

                    case GLFW_KEY_E:
                        zoom ^= ZOOM_VISION_ARRIERE;
                        break;
                }
            }
        }
    }
}
