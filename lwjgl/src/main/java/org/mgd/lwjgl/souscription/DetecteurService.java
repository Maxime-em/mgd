package org.mgd.lwjgl.souscription;

import org.lwjgl.glfw.*;
import org.mgd.lwjgl.Fenetre;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class DetecteurService {
    private static DetecteurService detecteurService;
    private final Set<DetecteurErreur> erreurs;
    private final Map<Long, Fenetre> fenetres;
    private final Map<Fenetre, List<DetecteurClavier>> claviers;
    private final Map<Fenetre, List<DetecteurPositionSouris>> positionsSouris;
    private final Map<Fenetre, List<DetecteurInterieurSouris>> interieursSouris;
    private final Map<Fenetre, List<DetecteurBoutonSouris>> boutonsSouris;
    private final GLFWErrorCallback gestionErreurs = new GLFWErrorCallback() {
        @Override
        public void invoke(int erreur, long description) {
            erreurs.forEach(dectecteur -> dectecteur.invoquer(erreur, description));
        }
    };
    private final GLFWKeyCallback gestionClavier = new GLFWKeyCallback() {
        @Override
        public void invoke(long identifiant, int cle, int code, int action, int modifications) {
            claviers.getOrDefault(fenetres.get(identifiant), Collections.emptyList()).forEach(dectecteur -> dectecteur.invoquer(cle, code, action, modifications));
        }
    };
    private final GLFWMouseButtonCallback gestionBoutonSouris = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long identifiant, int bouton, int action, int mode) {
            boutonsSouris.getOrDefault(fenetres.get(identifiant), Collections.emptyList()).forEach(dectecteur -> dectecteur.invoquer(bouton, action, mode));
        }
    };
    private final GLFWCursorPosCallback gestionPositionSouris = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long identifiant, double abscisse, double ordonnee) {
            positionsSouris.getOrDefault(fenetres.get(identifiant), Collections.emptyList()).forEach(dectecteur -> dectecteur.invoquer(abscisse, ordonnee));
        }
    };
    private final GLFWCursorEnterCallback gestionInterieurSouris = new GLFWCursorEnterCallback() {
        @Override
        public void invoke(long identifiant, boolean interieur) {
            interieursSouris.getOrDefault(fenetres.get(identifiant), Collections.emptyList()).forEach(dectecteur -> dectecteur.invoquer(interieur));
        }
    };

    private DetecteurService() {
        this.fenetres = new HashMap<>();
        this.erreurs = new LinkedHashSet<>();
        this.claviers = new HashMap<>();
        this.positionsSouris = new HashMap<>();
        this.interieursSouris = new HashMap<>();
        this.boutonsSouris = new HashMap<>();

        glfwSetErrorCallback(gestionErreurs);
    }

    public static DetecteurService obtenir() {
        if (detecteurService == null) {
            detecteurService = new DetecteurService();
        }
        return detecteurService;
    }

    public void souscrire(DetecteurErreur erreur) {
        erreurs.add(erreur);
    }

    public void souscrire(Fenetre fenetre, DetecteurClavier clavier) {
        fenetres.putIfAbsent(fenetre.identifiant(), fenetre);
        claviers.computeIfAbsent(fenetre, cle -> {
            glfwSetKeyCallback(cle.identifiant(), gestionClavier);
            return new LinkedList<>();
        }).add(clavier);
    }

    public void souscrire(Fenetre fenetre, DetecteurPositionSouris positionSouris) {
        fenetres.putIfAbsent(fenetre.identifiant(), fenetre);
        positionsSouris.computeIfAbsent(fenetre, cle -> {
            glfwSetCursorPosCallback(cle.identifiant(), gestionPositionSouris);
            return new LinkedList<>();
        }).add(positionSouris);
    }

    public void souscrire(Fenetre fenetre, DetecteurInterieurSouris interieurSouris) {
        fenetres.putIfAbsent(fenetre.identifiant(), fenetre);
        interieursSouris.computeIfAbsent(fenetre, cle -> {
            glfwSetCursorEnterCallback(cle.identifiant(), gestionInterieurSouris);
            return new LinkedList<>();
        }).add(interieurSouris);
    }

    public void souscrire(Fenetre fenetre, DetecteurBoutonSouris boutonSouris) {
        fenetres.putIfAbsent(fenetre.identifiant(), fenetre);
        boutonsSouris.computeIfAbsent(fenetre, cle -> {
            glfwSetMouseButtonCallback(cle.identifiant(), gestionBoutonSouris);
            return new LinkedList<>();
        }).add(boutonSouris);
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
