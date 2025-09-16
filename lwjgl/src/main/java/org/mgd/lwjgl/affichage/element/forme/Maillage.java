package org.mgd.lwjgl.affichage.element.forme;

import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryStack;
import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Contour;
import org.mgd.lwjgl.EtatVisuel;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Programme;
import org.mgd.lwjgl.Vue;
import org.mgd.lwjgl.affichage.element.GroupeFormes;
import org.mgd.lwjgl.affichage.transition.Transition;
import org.mgd.lwjgl.affichage.transition.TransitionTableauFlottants;
import org.mgd.lwjgl.commun.Sujet;
import org.mgd.lwjgl.interne.ChargeurTextures;
import org.mgd.lwjgl.interne.GestionnaireOmbrages;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;
import static org.mgd.lwjgl.Programme.NOM_OMBRAGE_PAR_DEFAUT;

public abstract class Maillage implements Sujet {
    protected final UUID uuid;
    protected final GroupeFormes<?> parent;
    protected final String nom;
    protected final Matrice<Float> deplacement;
    private final LinkedList<TransitionDeplacement> transitions;
    private final int taille;
    private final int vecteurs;
    private final Set<Integer> tampons;
    private final Matrice<Float> gravite;
    private final Matrice<Float> boite;
    protected Contour contour;
    protected boolean survole;
    protected boolean active;
    protected boolean visible;
    private boolean libere;

    protected Maillage(GroupeFormes<?> parent, String nom, float[] positions, float[] boite, float[] textures, int[] indices) {
        verifierGeometrie(positions, boite, textures, indices);
        this.uuid = UUID.randomUUID();
        this.parent = parent;
        this.nom = nom;
        Set<Integer> tamponsCrees = new HashSet<>();
        int identifiantVecteurs = glGenVertexArrays();
        try (MemoryStack pile = MemoryStack.stackPush()) {
            if (identifiantVecteurs == 0) {
                throw new IllegalStateException("Impossible de créer le tableau de sommets OpenGL.");
            }
            glBindVertexArray(identifiantVecteurs);

            int identifiant = creerTampon(tamponsCrees);
            FloatBuffer flottants = pile.mallocFloat(positions.length);
            flottants.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, identifiant);
            glBufferData(GL_ARRAY_BUFFER, flottants, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            identifiant = creerTampon(tamponsCrees);
            IntBuffer entiers = pile.mallocInt(indices.length);
            entiers.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, identifiant);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, entiers, GL_STATIC_DRAW);

            identifiant = creerTampon(tamponsCrees);
            flottants = pile.mallocFloat(textures.length);
            flottants.put(textures).flip();
            glBindBuffer(GL_ARRAY_BUFFER, identifiant);
            glBufferData(GL_ARRAY_BUFFER, flottants, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        } catch (RuntimeException | Error erreur) {
            tamponsCrees.forEach(GL15::glDeleteBuffers);
            if (identifiantVecteurs != 0) {
                glDeleteVertexArrays(identifiantVecteurs);
            }
            throw erreur;
        } finally {
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        this.taille = indices.length;
        this.vecteurs = identifiantVecteurs;
        this.tampons = tamponsCrees;
        this.deplacement = Matrice.identitef(4, 4);
        this.transitions = new LinkedList<>();
        this.gravite = Matrice.translation(centreNegatif(positions));
        this.boite = Matrice.identitef(4, boite.length / 3).insererParLignes((ligne, colonne, _) -> ligne % 4 == 3 ? 1f : boite[colonne * 3 + ligne]);
    }

    private static int creerTampon(Set<Integer> tamponsCrees) {
        int identifiant = glGenBuffers();
        if (identifiant == 0) {
            throw new IllegalStateException("Impossible de créer un tampon OpenGL.");
        }
        tamponsCrees.add(identifiant);
        return identifiant;
    }

    private static void verifierGeometrie(float[] positions, float[] boite, float[] textures, int[] indices) {
        Objects.requireNonNull(positions, "Les positions ne peuvent pas être null.");
        Objects.requireNonNull(boite, "La boîte ne peut pas être null.");
        Objects.requireNonNull(textures, "Les coordonnées de texture ne peuvent pas être null.");
        Objects.requireNonNull(indices, "Les indices ne peuvent pas être null.");
        if (positions.length < 9 || positions.length % 3 != 0 || boite.length < 9 || boite.length % 3 != 0
                || textures.length != positions.length / 3 * 2 || indices.length == 0 || indices.length % 3 != 0
                || contientValeurNonFinie(positions) || contientValeurNonFinie(boite) || contientValeurNonFinie(textures)) {
            throw new IllegalArgumentException("La géométrie doit contenir des sommets 3D, des coordonnées de texture et des indices valides.");
        }
        int nombreSommets = positions.length / 3;
        if (Arrays.stream(indices).anyMatch(index -> index < 0 || index >= nombreSommets)) {
            throw new IllegalArgumentException("Un indice de la géométrie référence un sommet inexistant.");
        }
    }

    private static boolean contientValeurNonFinie(float[] valeurs) {
        for (float valeur : valeurs) {
            if (!Float.isFinite(valeur)) {
                return true;
            }
        }
        return false;
    }

    private static float[] centreNegatif(float[] positions) {
        double x = 0;
        double y = 0;
        double z = 0;
        int nombreSommets = positions.length / 3;
        for (int index = 0; index < positions.length; index += 3) {
            x += positions[index];
            y += positions[index + 1];
            z += positions[index + 2];
        }
        return new float[]{(float) (-x / nombreSommets), (float) (-y / nombreSommets), (float) (-z / nombreSommets)};
    }

    public void desurvoler() {
        survole = false;
    }

    public void activer() {
        active = true;
    }

    public void desactiver() {
        active = false;
    }

    public void nettoyer() {
        if (libere) {
            return;
        }
        libere = true;
        tampons.forEach(GL15::glDeleteBuffers);
        tampons.clear();
        glDeleteVertexArrays(vecteurs);
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public void apparaitre() {
        visible = true;
    }

    @Override
    public void disparaitre() {
        visible = false;
    }

    @Override
    public boolean survoler(Vue vue, EvenementSouris evenementSouris) {
        survole = evenementSouris.calcul() && contour.intersecter(evenementSouris.coordonneesVision());
        return survole;
    }

    @Override
    public void maj(long accumulateur, Vue vue, EvenementSouris evenementSouris, EvenementAmorcages evenementAmorcagesCourant) {
        if (!transitions.isEmpty()) {
            transitions.getFirst().lineariser(accumulateur);
            transitions.removeIf(Transition::finie);
        }
    }

    @Override
    public void produire(long tempsEcouleMs, Vue vue) {
        if (visible) {
            Optional<Integer> textureBase = ChargeurTextures.obtenir(parent.identifiant(), EtatVisuel.PSEUDO_BASE);
            Optional<Integer> textureSurvole = ChargeurTextures.obtenir(parent.identifiant(), EtatVisuel.PSEUDO_SURVOLE);
            Optional<Integer> textureActiver = ChargeurTextures.obtenir(parent.identifiant(), EtatVisuel.PSEUDO_ACTIVER);

            GestionnaireOmbrages.configurer("deplacement", deplacement);
            GestionnaireOmbrages.configurer("echantillonneur", 0);
            if (textureBase.isPresent()) {
                glActiveTexture(GL_TEXTURE0);
                if (textureActiver.isPresent() && active) {
                    glBindTexture(GL_TEXTURE_2D, textureActiver.get());
                } else if (textureSurvole.isPresent() && survole) {
                    glBindTexture(GL_TEXTURE_2D, textureSurvole.get());
                } else {
                    glBindTexture(GL_TEXTURE_2D, textureBase.get());
                }
            }

            Map<String, Programme> programmes = GestionnaireOmbrages.programmes(nom);
            if (programmes.containsKey(EtatVisuel.PSEUDO_ACTIVER) && active) {
                GestionnaireOmbrages.utiliser(programmes.get(EtatVisuel.PSEUDO_ACTIVER));
            } else if (programmes.containsKey(EtatVisuel.PSEUDO_SURVOLE) && survole) {
                GestionnaireOmbrages.utiliser(programmes.get(EtatVisuel.PSEUDO_SURVOLE));
            } else if (programmes.containsKey(EtatVisuel.PSEUDO_BASE)) {
                GestionnaireOmbrages.utiliser(programmes.get(EtatVisuel.PSEUDO_BASE));
            } else {
                GestionnaireOmbrages.utiliser(GestionnaireOmbrages.programmes(NOM_OMBRAGE_PAR_DEFAUT).get(EtatVisuel.PSEUDO_BASE));
            }

            glBindVertexArray(vecteurs);
            glDrawElements(GL_TRIANGLES, taille, GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);
        }
    }

    public void deplacer(float[] position, long duree) {
        Float[] depart = transitions.isEmpty()
                ? new Float[]{deplacement.valeur(0, 3), deplacement.valeur(1, 3), deplacement.valeur(2, 3)}
                : transitions.getLast().arrive();
        Float[] arrivee = gravite.multiplication(Matrice.vecteur(position)).colonne(0);
        transitions.addLast(new TransitionDeplacement(depart, arrivee, duree));
    }

    public void preparer(Vue vue) {
        // Coordonnées du contour de la forme dans le référentiel de la vision
        contour = new Contour(vue.matrice().multiplication(parent.transformation()).multiplication(deplacement).multiplication(boite));
    }

    public Matrice<Float> projeterPlanEcran() {
        return contour.projeterPlanEcran();
    }

    private class TransitionDeplacement extends TransitionTableauFlottants<Matrice<Float>> {
        public TransitionDeplacement(Float[] depart, Float[] arrivee, long duree) {
            super(deplacement, depart, arrivee, duree);
        }

        @Override
        protected void appliquer(Matrice<Float> matrice, Float[] valeur) {
            matrice.modifierValeur(0, 3, valeur[0], (_, nouvelle) -> nouvelle);
            matrice.modifierValeur(1, 3, valeur[1], (_, nouvelle) -> nouvelle);
            matrice.modifierValeur(2, 3, valeur[2], (_, nouvelle) -> nouvelle);
        }
    }
}
