package org.mgd.lwjgl.interne;

import org.lwjgl.glfw.*;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class DetecteurService {
    private final Set<DetecteurErreur> erreurs;
    private final Map<Long, List<DetecteurClavier>> claviers;
    private final Map<Long, List<DetecteurPositionSouris>> positionsSouris;
    private final Map<Long, List<DetecteurInterieurSouris>> interieursSouris;
    private final Map<Long, List<DetecteurBoutonSouris>> boutonsSouris;
    private final GLFWErrorCallback gestionErreurs = new GLFWErrorCallback() {
        @Override
        public void invoke(int identifiant, long cle) {
            erreurs.forEach(erreur -> erreur.invoquer(cle));
        }
    };
    private final GLFWKeyCallback gestionClavier = new GLFWKeyCallback() {
        @Override
        public void invoke(long identifiant, int cle, int code, int action, int modifications) {
            claviers.getOrDefault(identifiant, Collections.emptyList()).forEach(dectecteur -> dectecteur.invoquer(cle, code, action, modifications));
        }
    };
    private final GLFWMouseButtonCallback gestionBoutonSouris = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long identifiant, int bouton, int action, int mode) {
            boutonsSouris.getOrDefault(identifiant, Collections.emptyList()).forEach(dectecteur -> dectecteur.invoquer(bouton, action, mode));
        }
    };
    private final GLFWCursorPosCallback gestionPositionSouris = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long identifiant, double abscisse, double ordonnee) {
            positionsSouris.getOrDefault(identifiant, Collections.emptyList()).forEach(dectecteur -> dectecteur.invoquer(abscisse, ordonnee));
        }
    };
    private final GLFWCursorEnterCallback gestionInterieurSouris = new GLFWCursorEnterCallback() {
        @Override
        public void invoke(long identifiant, boolean interieur) {
            interieursSouris.getOrDefault(identifiant, Collections.emptyList()).forEach(dectecteur -> dectecteur.invoquer(interieur));
        }
    };

    private DetecteurService() {
        this.erreurs = new LinkedHashSet<>();
        this.claviers = new HashMap<>();
        this.positionsSouris = new HashMap<>();
        this.interieursSouris = new HashMap<>();
        this.boutonsSouris = new HashMap<>();

        glfwSetErrorCallback(gestionErreurs);
    }

    public static DetecteurService obtenir() {
        return new DetecteurService();
    }

    public DetecteurService souscrire(DetecteurErreur erreur) {
        erreurs.add(erreur);
        return this;
    }

    public DetecteurService souscrire(long identifiant, DetecteurClavier clavier) {
        claviers.computeIfAbsent(identifiant, cle -> {
            glfwSetKeyCallback(identifiant, gestionClavier);
            return new LinkedList<>();
        }).add(clavier);
        return this;
    }

    public DetecteurService souscrire(long identifiant, DetecteurPositionSouris positionSouris) {
        positionsSouris.computeIfAbsent(identifiant, cle -> {
            glfwSetCursorPosCallback(identifiant, gestionPositionSouris);
            return new LinkedList<>();
        }).add(positionSouris);
        return this;
    }

    public DetecteurService souscrire(long identifiant, DetecteurInterieurSouris interieurSouris) {
        interieursSouris.computeIfAbsent(identifiant, cle -> {
            glfwSetCursorEnterCallback(identifiant, gestionInterieurSouris);
            return new LinkedList<>();
        }).add(interieurSouris);
        return this;
    }

    public DetecteurService souscrire(long identifiant, DetecteurBoutonSouris boutonSouris) {
        boutonsSouris.computeIfAbsent(identifiant, cle -> {
            glfwSetMouseButtonCallback(identifiant, gestionBoutonSouris);
            return new LinkedList<>();
        }).add(boutonSouris);
        return this;
    }

    public void nettoyer() {
        erreurs.clear();
        gestionErreurs.free();

        claviers.clear();
        gestionClavier.free();

        gestionPositionSouris.free();
        gestionInterieurSouris.free();
        gestionBoutonSouris.free();
    }
}
