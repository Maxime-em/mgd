package org.mgd.lwjgl;

import org.lwjgl.glfw.GLFWVidMode;
import org.mgd.commun.IConstantesMathematiques;
import org.mgd.commun.Matrice;
import org.mgd.lwjgl.element.Element;
import org.mgd.lwjgl.element.ElementActivable;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.interne.Ombreur;
import org.mgd.lwjgl.souscription.DetecteurService;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
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
    private final LinkedList<Element> enfants;
    private final LinkedList<AffichageTeteHaute> affichages;
    private final int ratioNumerateur;
    private final int ratioDenominateur;
    private final EvenementSouris evenementSouris;
    private final EvenementClavier evenementClavier;

    private long contexteNvg;
    private AffichageTeteHaute menu;

    public Fenetre(String titre, int hauteur, int ratioNumerateur, int ratioDenominateur) throws LwjglException {
        this.vision = new Vision();
        this.enfants = new LinkedList<>();
        this.affichages = new LinkedList<>();
        this.evenementSouris = new EvenementSouris();
        this.evenementClavier = new EvenementClavier();

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
        this.ratioNumerateur = ratioNumerateur;
        this.ratioDenominateur = ratioDenominateur;
        this.hauteur = hauteur;
        this.largeur = this.ratioNumerateur * this.hauteur / this.ratioDenominateur;
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
        DetecteurService detecteurService = DetecteurService.obtenir();
        detecteurService.souscrire(this, this.evenementClavier::gerer);
        detecteurService.souscrire(this, this.evenementSouris::gererInterieur);
        detecteurService.souscrire(this, this.evenementSouris::gererPosition);
        detecteurService.souscrire(this, this.evenementSouris::gererSelection);
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

    protected void ajouterMenu(AffichageTeteHaute menu) {
        this.menu = menu;
    }

    public void creerContexteNvg() throws LwjglException {
        this.contexteNvg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (this.contexteNvg == 0L) {
            throw new LwjglException("Impossible d'initialiser NanoVG.");
        }
    }

    public AffichageTeteHaute.NVGPolice creerPolice(String identifiant, Path fichier) {
        return new AffichageTeteHaute.NVGPolice(identifiant, fichier, nvgCreateFont(contexteNvg, identifiant, fichier.toString()));
    }

    public AffichageTeteHaute.NVGImage creerImage(String identifiant, Path fichier) {
        int nvg = nvgCreateImage(contexteNvg, fichier.toString(), NVG_IMAGE_NEAREST);
        int[] largeurImage = new int[1];
        int[] hauteurImage = new int[1];
        nvgImageSize(contexteNvg, nvg, largeurImage, hauteurImage);
        return new AffichageTeteHaute.NVGImage(identifiant, fichier, largeurImage[0], hauteurImage[0], nvg);
    }

    public void maj() {
        EvenementClavier evenementClavier = this.evenementClavier.clone();
        EvenementSouris evenementSouris = this.evenementSouris.clone();
        if (menu != null && menu.visible()) {
            menu.maj(vision, evenementSouris);
        } else {
            switch (evenementClavier.deplacement) {
                case DEPLACEMENT_VISION_HAUT -> vision.translater(0f, 0.01f, 0f);
                case DEPLACEMENT_VISION_HAUT | DEPLACEMENT_VISION_DROITE -> vision.translater(0.01f, 0.01f, 0f);
                case DEPLACEMENT_VISION_DROITE -> vision.translater(0.01f, 0f, 0f);
                case DEPLACEMENT_VISION_BAS | DEPLACEMENT_VISION_DROITE -> vision.translater(0.01f, -0.01f, 0f);
                case DEPLACEMENT_VISION_BAS -> vision.translater(0f, -0.01f, 0f);
                case DEPLACEMENT_VISION_BAS | DEPLACEMENT_VISION_GAUCHE -> vision.translater(-0.01f, -0.01f, 0f);
                case DEPLACEMENT_VISION_GAUCHE -> vision.translater(-0.01f, 0f, 0f);
                case DEPLACEMENT_VISION_HAUT | DEPLACEMENT_VISION_GAUCHE -> vision.translater(-0.01f, 0.01f, 0f);
            }

            switch (evenementClavier.zoom) {
                case ZOOM_VISION_ARRIERE -> vision.translater(0f, 0f, 0.01f);
                case ZOOM_VISION_AVANT -> vision.translater(0f, 0f, -0.01f);
            }

            affichages.forEach(affichage -> affichage.maj(vision, evenementSouris));
            enfants.forEach(enfant -> {
                enfant.maj(vision, evenementSouris);
                if ((enfant instanceof ElementActivable<?, ?> activable) && evenementSouris.inacheve() && evenementSouris.selection()) {
                    activable.deactiver();
                }
            });
        }
    }

    public void produire() {
        Ombreur.specifier("projection", Matrice.projection60());
        if (menu != null && menu.visible()) {
            menu.produire(vision);
        } else {
            vision.produire();
            enfants.forEach(enfant -> enfant.produire(vision));
            affichages.forEach(affichage -> affichage.produire(vision));
        }
    }

    public void liberer() {
        glfwDestroyWindow(identifiant);
        glfwFreeCallbacks(identifiant);

        vision.liberer();
        menu.liberer();
        enfants.forEach(Element::nettoyer);
        affichages.forEach(AffichageTeteHaute::liberer);

        glfwTerminate();
    }

    public long identifiant() {
        return identifiant;
    }

    public long contexteNvg() {
        return contexteNvg;
    }

    public int largeur() {
        return largeur;
    }

    public int hauteur() {
        return hauteur;
    }

    public List<Element> enfants() {
        return enfants;
    }

    public LinkedList<AffichageTeteHaute> affichages() {
        return affichages;
    }

    public static class Evenement implements Cloneable {
        protected boolean accompli = true;

        public void comsommer() {
            accompli = true;
        }

        public boolean inacheve() {
            return !accompli;
        }

        @Override
        public Evenement clone() {
            try {
                Evenement clone = (Evenement) super.clone();
                clone.accompli = false;
                accompli = true;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    private static final class EvenementClavier extends Evenement {
        private int deplacement;
        private int zoom;

        public EvenementClavier() {
            this.deplacement = DEPLACEMENT_VISION_AUCUN;
            this.zoom = ZOOM_VISION_AUCUN;
        }

        public void gerer(int cle, int code, int action, int modifications) {
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

        @Override
        public EvenementClavier clone() {
            EvenementClavier clone = (EvenementClavier) super.clone();
            clone.deplacement = deplacement;
            clone.zoom = zoom;
            return clone;
        }
    }

    public class EvenementSouris extends Evenement {
        private final float[] coordonnees;
        private final float[] coordonneesHomogenes;
        private final float[] direction;
        private boolean calcul;
        private boolean selection;

        public EvenementSouris() {
            this.coordonnees = new float[2];
            this.coordonneesHomogenes = new float[2];
            this.direction = new float[3];
        }

        public boolean inclus(float abscisse, float ordonnee, float largeur, float hauteur) {
            return abscisse <= coordonnees[0] && coordonnees[0] <= abscisse + largeur
                    && ordonnee <= coordonnees[1] && coordonnees[1] <= ordonnee + hauteur;
        }

        public boolean inclus(AffichageTeteHaute.Bouton bouton) {
            return inclus(bouton.position[0], bouton.position[1], bouton.dimensions[2], bouton.dimensions[3]);
        }

        public boolean inclus(AffichageTeteHaute.Action action) {
            return inclus(action.abscisse(), action.ordonnee(), action.largeur(), action.hauteur());
        }

        public float[] coordonneesHomogenes() {
            return coordonneesHomogenes;
        }

        public float[] direction() {
            return direction;
        }

        public boolean calcul() {
            return calcul;
        }

        public boolean selection() {
            return selection;
        }

        protected void gererPosition(double abscisse, double ordonnee) {
            coordonnees[0] = Double.valueOf(abscisse).floatValue();
            coordonnees[1] = Double.valueOf(ordonnee).floatValue();
            coordonneesHomogenes[0] = 2f * coordonnees[0] / largeur - 1f;
            coordonneesHomogenes[1] = 1f - 2f * coordonnees[1] / hauteur;
            direction[0] = ratioNumerateur * (2f * coordonnees[0] - largeur) / (IConstantesMathematiques.RACINE_TROIS * largeur * ratioDenominateur);
            direction[1] = (hauteur - 2f * coordonnees[1]) / (IConstantesMathematiques.RACINE_TROIS * hauteur);
            direction[2] = -1f;
        }

        protected void gererInterieur(boolean interieur) {
            calcul = interieur;
        }

        protected void gererSelection(int bouton, int action, int mode) {
            if (bouton == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                selection = true;
            }
        }

        @Override
        public EvenementSouris clone() {
            EvenementSouris clone = (EvenementSouris) super.clone();
            System.arraycopy(coordonnees, 0, clone.coordonnees, 0, coordonnees.length);
            System.arraycopy(coordonneesHomogenes, 0, clone.coordonneesHomogenes, 0, coordonneesHomogenes.length);
            System.arraycopy(direction, 0, clone.direction, 0, direction.length);
            clone.calcul = calcul;
            clone.selection = selection;
            selection = false;
            return clone;
        }
    }
}
