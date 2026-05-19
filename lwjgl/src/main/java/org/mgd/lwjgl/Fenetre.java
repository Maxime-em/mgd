package org.mgd.lwjgl;

import org.lwjgl.glfw.GLFWVidMode;
import org.mgd.commun.Matrice;
import org.mgd.lwjgl.affichage.Primitif;
import org.mgd.lwjgl.affichage.element.Element;
import org.mgd.lwjgl.affichage.tetehaute.AffichageTeteHaute;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGImage;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGPolice;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.souscription.DetecteurAmorcage;
import org.mgd.lwjgl.souscription.DetecteurService;
import org.mgd.lwjgl.souscription.Identifiable;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Fenetre implements Identifiable {
    private static final float DEPLACEMENT_VITESSE = 2.5f;
    private static final int DEPLACEMENT_VISION_AUCUN = 0x00;
    private static final int DEPLACEMENT_VISION_HAUT = 0x01;
    private static final int DEPLACEMENT_VISION_DROITE = 0x02;
    private static final int DEPLACEMENT_VISION_BAS = 0x04;
    private static final int DEPLACEMENT_VISION_GAUCHE = 0x08;

    private static final int ZOOM_VISION_AUCUN = 0x00;
    private static final int ZOOM_VISION_AVANT = 0x01;
    private static final int ZOOM_VISION_ARRIERE = 0x02;

    private final UUID uuid;
    private final long identifiant;
    private final int largeur;
    private final int hauteur;

    private final Homogeneite homogeneite;
    private final Projection projection;
    private final Vision vision;
    private final SortedSet<Element<?>> enfants;
    private final LinkedList<AffichageTeteHaute> affichages;
    private final EvenementSouris evenementSouris;
    private final EvenementClavier evenementClavier;
    private final EvenementAmorcages evenementAmorcages;
    private final Map<UUID, Consumer<Amorcage>> invocationsUnitaires;
    private final Map<String, Collection<? extends Identifiable>> groupes;
    private final Map<String, Consumer<Amorcage>> invocationsGroupees;

    private long contexteNvg;
    private Map<String, NVGPolice> polices;
    private Map<String, NVGImage> images;
    private AffichageTeteHaute menu;

    protected Fenetre(String titre, int hauteur, int ratioNumerateur, int ratioDenominateur) throws LwjglException {
        this.uuid = UUID.randomUUID();
        this.vision = new Vision();
        this.enfants = new TreeSet<>(Comparator.<Element<?>, Integer>comparing(Element::priorite).reversed().thenComparing(Element::identifiant));
        this.affichages = new LinkedList<>();
        this.evenementSouris = new EvenementSouris();
        this.evenementClavier = new EvenementClavier();
        this.evenementAmorcages = new EvenementAmorcages();
        this.invocationsUnitaires = new HashMap<>();
        this.groupes = new HashMap<>();
        this.invocationsGroupees = new HashMap<>();

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
        this.hauteur = hauteur;
        this.largeur = ratioNumerateur * this.hauteur / ratioDenominateur;
        this.homogeneite = new Homogeneite(this.hauteur, this.largeur);
        this.projection = new Projection(ratioNumerateur, ratioDenominateur);
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

    public void ajouterMenu(AffichageTeteHaute menu) {
        this.menu = menu;
    }

    public void apparaitre() {
        menu.disparaitre();
        enfants.forEach(Primitif::apparaitre);
        affichages.stream().filter(Primitif::apparaitreParDefaut).forEach(Primitif::apparaitre);
    }

    public void creerContexteNvg() throws LwjglException {
        this.contexteNvg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (this.contexteNvg == 0L) {
            throw new LwjglException("Impossible d'initialiser NanoVG.");
        }
        this.polices = new HashMap<>();
        this.images = new HashMap<>();
    }

    public void creerPolice(String identifiant, Path fichier) {
        polices.computeIfAbsent(identifiant, _ -> new NVGPolice(identifiant, fichier, nvgCreateFont(contexteNvg, identifiant, fichier.toString())));
    }

    public void creerImage(String identifiant, Path fichier) {
        images.computeIfAbsent(identifiant, _ -> {
            int nvg = nvgCreateImage(contexteNvg, fichier.toString(), NVG_IMAGE_NEAREST);
            int[] largeurImage = new int[1];
            int[] hauteurImage = new int[1];
            nvgImageSize(contexteNvg, nvg, largeurImage, hauteurImage);
            return new NVGImage(identifiant, fichier, largeurImage[0], hauteurImage[0], nvg);
        });
    }

    public NVGImage obtenirImage(String identifiant) {
        if (!images.containsKey(identifiant)) {
            throw new NoSuchElementException(MessageFormat.format("L''image \"{0}\" est introuvable.", identifiant));
        }
        return images.get(identifiant);
    }

    public NVGPolice obtenirPolice(String identifiant) {
        if (!polices.containsKey(identifiant)) {
            throw new NoSuchElementException(MessageFormat.format("La police \"{0}\" est introuvable.", identifiant));
        }
        return polices.get(identifiant);
    }

    public void maj(long accumulateur) throws LwjglException {
        EvenementClavier evenementClavierCourant = new EvenementClavier(evenementClavier);
        EvenementSouris evenementSourisCourant = new EvenementSouris(evenementSouris);
        EvenementAmorcages evenementAmorcagesCourant = new EvenementAmorcages(evenementAmorcages);
        if (menu != null && menu.visible()) {
            menu.maj(vision, evenementSourisCourant, evenementAmorcagesCourant);
            notifier(evenementAmorcagesCourant);
        } else {
            float decalage = accumulateur * DEPLACEMENT_VITESSE / 1000;
            switch (evenementClavierCourant.deplacement) {
                case DEPLACEMENT_VISION_HAUT -> vision.translater(0f, decalage, 0f);
                case DEPLACEMENT_VISION_HAUT | DEPLACEMENT_VISION_DROITE -> vision.translater(decalage, decalage, 0f);
                case DEPLACEMENT_VISION_DROITE -> vision.translater(decalage, 0f, 0f);
                case DEPLACEMENT_VISION_BAS | DEPLACEMENT_VISION_DROITE -> vision.translater(decalage, -decalage, 0f);
                case DEPLACEMENT_VISION_BAS -> vision.translater(0f, -decalage, 0f);
                case DEPLACEMENT_VISION_BAS | DEPLACEMENT_VISION_GAUCHE -> vision.translater(-decalage, -decalage, 0f);
                case DEPLACEMENT_VISION_GAUCHE -> vision.translater(-decalage, 0f, 0f);
                case DEPLACEMENT_VISION_HAUT | DEPLACEMENT_VISION_GAUCHE -> vision.translater(-decalage, decalage, 0f);
                default -> {// Rien à faire
                }
            }

            switch (evenementClavierCourant.zoom) {
                case ZOOM_VISION_ARRIERE -> vision.translater(0f, 0f, decalage);
                case ZOOM_VISION_AVANT -> vision.translater(0f, 0f, -decalage);
                default -> {// Rien à faire
                }
            }

            notifier(evenementAmorcagesCourant);

            enfants.forEach(enfant -> enfant.preparer(vision));
            for (AffichageTeteHaute affichage : affichages) {
                affichage.maj(vision, evenementSourisCourant, evenementAmorcagesCourant);
            }
            for (Element<?> enfant : enfants) {
                enfant.maj(vision, evenementSourisCourant, evenementAmorcagesCourant);
            }

            if (evenementSourisCourant.inacheve() && evenementSourisCourant.selection()) {
                amorcer(Collections.singleton(this), evenementSourisCourant.droite());
                evenementSourisCourant.comsommer();
            }
        }
    }

    public void produire(long ellipse) {
        if (menu != null && menu.visible()) {
            menu.produire(ellipse, vision);
        } else {
            projection.produire();
            vision.produire();
            enfants.forEach(enfant -> enfant.produire(ellipse, vision));
            affichages.forEach(affichage -> affichage.produire(ellipse, vision));
        }
    }

    public void liberer() {
        glfwDestroyWindow(identifiant);
        glfwFreeCallbacks(identifiant);

        nvgDelete(contexteNvg);

        homogeneite.liberer();
        projection.liberer();
        vision.liberer();
        if (menu != null) {
            menu.liberer();
        }
        enfants.forEach(Element::nettoyer);
        affichages.forEach(AffichageTeteHaute::liberer);

        glfwTerminate();
    }

    public void fermer() {
        glfwSetWindowShouldClose(identifiant, true);
    }

    public void amorcer(Collection<Identifiable> cles, boolean droite) {
        evenementAmorcages.ajouter(cles.stream().map(Identifiable::uuid).toList(), droite);
    }

    public <T extends Identifiable> void souscrire(T identifiable, DetecteurAmorcage<T> gauche) {
        souscrireInterne(identifiable, amorcage -> {
            if (!amorcage.droite()) {
                gauche.invoquer(identifiable);
            }
        });
    }

    private <T extends Identifiable> void souscrireInterne(T identifiable, Consumer<Amorcage> invocation) {
        invocationsUnitaires.put(identifiable.uuid(), invocation);
    }

    public <T extends Identifiable> void souscrire(String groupe, Collection<T> identifiables, DetecteurAmorcage<T> gauche) {
        souscrireInterne(groupe, identifiables, (amorcage, identifiable) -> {
            if (!amorcage.droite()) {
                gauche.invoquer(identifiable);
            }
        });
    }

    public <T extends Identifiable> void souscrire(String groupe, Collection<T> identifiables, DetecteurAmorcage<T> gauche, DetecteurAmorcage<T> droite) {
        souscrireInterne(groupe, identifiables, (amorcage, identifiable) -> {
            if (amorcage.droite()) {
                droite.invoquer(identifiable);
            } else {
                gauche.invoquer(identifiable);
            }
        });
    }

    private <T extends Identifiable> void souscrireInterne(String groupe, Collection<T> identifiables, BiConsumer<Amorcage, T> invocation) {
        groupes.put(groupe, identifiables);
        invocationsGroupees.put(groupe, amorcage -> identifiables.stream()
                .filter(identifiable -> identifiable.uuid().equals(amorcage.uuid()))
                .findFirst()
                .ifPresent(identifiable -> invocation.accept(amorcage, identifiable)));
    }

    public void souscrire(DetecteurAmorcage<Fenetre> detecteur) {
        invocationsUnitaires.put(uuid, _ -> detecteur.invoquer(this));
    }

    public void notifier(EvenementAmorcages evenementAmorcages) {
        evenementAmorcages.amorcages().forEach(amorcage -> {
            invocationsUnitaires.entrySet()
                    .stream()
                    .filter(element -> element.getKey().equals(amorcage.uuid()))
                    .forEach(element -> element.getValue().accept(amorcage));
            groupes.entrySet()
                    .stream()
                    .filter(element -> element.getValue().stream().anyMatch(identifiable -> identifiable.uuid().equals(amorcage.uuid())))
                    .forEach(element -> invocationsGroupees.get(element.getKey()).accept(amorcage));
        });
    }

    @Override
    public UUID uuid() {
        return uuid;
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

    public Homogeneite homogeneite() {
        return homogeneite;
    }

    public Projection projection() {
        return projection;
    }

    public SortedSet<Element<?>> enfants() {
        return enfants;
    }

    public List<AffichageTeteHaute> affichages() {
        return affichages;
    }

    public static class Evenement {
        protected boolean accompli;

        public void comsommer() {
            accompli = true;
        }

        public boolean inacheve() {
            return !accompli;
        }
    }

    private static final class EvenementClavier extends Evenement {
        private int deplacement;
        private int zoom;

        public EvenementClavier() {
            this.deplacement = DEPLACEMENT_VISION_AUCUN;
            this.zoom = ZOOM_VISION_AUCUN;
        }

        public EvenementClavier(EvenementClavier evenement) {
            this.accompli = false;
            this.deplacement = evenement.deplacement;
            this.zoom = evenement.zoom;

            evenement.comsommer();
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

                    default:
                        // Rien à faire
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

                    default:
                        //Rien à faire
                        break;
                }
            }
        }
    }

    public static class EvenementAmorcages extends Evenement {
        private final Deque<Amorcage> amorcages;

        public EvenementAmorcages() {
            this.amorcages = new ArrayDeque<>();
        }

        public EvenementAmorcages(EvenementAmorcages evenement) {
            this();
            this.amorcages.addAll(evenement.amorcages);
            evenement.amorcages.clear();
            evenement.comsommer();
        }

        public void ajouter(Collection<UUID> cles, boolean droite) {
            amorcages.addAll(cles.stream().map(cle -> new Amorcage(cle, droite)).toList());
        }

        public Deque<Amorcage> amorcages() {
            return amorcages;
        }
    }

    public record Amorcage(UUID uuid, boolean droite) {
    }

    public class EvenementSouris extends Evenement {
        private final float[] coordonneesEcran;
        private final float[] coordonneesVision;
        private boolean calcul;
        private boolean selection;
        private boolean droite;

        public EvenementSouris() {
            this.coordonneesEcran = new float[3];
            this.coordonneesVision = new float[3];
        }

        public EvenementSouris(EvenementSouris evenement) {
            this.accompli = false;
            this.coordonneesEcran = new float[3];
            this.coordonneesVision = new float[3];
            this.calcul = evenement.calcul;
            this.selection = evenement.selection;
            this.droite = evenement.droite;

            System.arraycopy(evenement.coordonneesEcran, 0, this.coordonneesEcran, 0, this.coordonneesEcran.length);
            System.arraycopy(evenement.coordonneesVision, 0, this.coordonneesVision, 0, this.coordonneesVision.length);

            evenement.selection = false;
            evenement.droite = false;
            evenement.comsommer();
        }

        public boolean inclus(float abscisse, float ordonnee, float largeur, float hauteur) {
            return abscisse <= coordonneesEcran[0] && coordonneesEcran[0] <= abscisse + largeur
                    && ordonnee <= coordonneesEcran[1] && coordonneesEcran[1] <= ordonnee + hauteur;
        }

        public float[] coordonnesVision() {
            return coordonneesVision;
        }

        public boolean calcul() {
            return calcul;
        }

        public boolean selection() {
            return selection;
        }

        public boolean droite() {
            return droite;
        }

        protected void gererPosition(double abscisse, double ordonnee) {
            coordonneesEcran[0] = (float) abscisse;
            coordonneesEcran[1] = (float) ordonnee;
            coordonneesEcran[2] = -1f;
            // Coordonnées du clique dans le référentiel de la vision
            projection.inverse.multiplication(homogeneite.inverse).multiplication(Matrice.vecteur(coordonneesEcran)).copierf(coordonneesVision, Float.class::cast);
        }

        protected void gererInterieur(boolean interieur) {
            calcul = interieur;
        }

        protected void gererSelection(int bouton, int action, int mode) {
            if ((bouton == GLFW_MOUSE_BUTTON_LEFT || bouton == GLFW_MOUSE_BUTTON_RIGHT) && action == GLFW_PRESS) {
                selection = true;
                droite = bouton == GLFW_MOUSE_BUTTON_RIGHT;
            }
        }
    }
}
