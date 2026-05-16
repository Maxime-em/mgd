package org.mgd.lwjgl.affichage.element.forme;

import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryStack;
import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre.EvenementAmorcages;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Programme;
import org.mgd.lwjgl.Pseudo;
import org.mgd.lwjgl.Survolable;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Sujet;
import org.mgd.lwjgl.affichage.Transition;
import org.mgd.lwjgl.affichage.element.Element;
import org.mgd.lwjgl.interne.Ombreur;
import org.mgd.lwjgl.interne.Tisseur;
import org.mgd.lwjgl.souscription.Identifiable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;
import static org.mgd.lwjgl.Programme.NOM_PAR_DEFAUT;

public abstract class Forme implements Identifiable, Survolable, Sujet {
    protected final UUID uuid;
    protected final Element<?> parent;
    protected final String nom;
    protected final Matrice<Float> deplacement;
    private final LinkedList<Transition<Matrice<Float>, Float[]>> transitions;
    private final int taille;
    private final int vecteurs;
    private final Set<Integer> tanpom;
    private final Matrice<Float> gravite;
    private final Matrice<Float> contour;
    protected Boite boite;
    protected boolean survole;
    protected boolean active;

    protected Forme(Element<?> parent, String nom, float[] positions, float[] textures, int[] indices, float[] contour) {
        this.uuid = UUID.randomUUID();
        this.parent = parent;
        this.nom = nom;
        this.tanpom = new HashSet<>();
        try (MemoryStack pile = MemoryStack.stackPush()) {
            this.taille = indices.length;

            this.vecteurs = glGenVertexArrays();
            glBindVertexArray(this.vecteurs);

            int identifiant = glGenBuffers();
            FloatBuffer flottants = pile.mallocFloat(positions.length);
            flottants.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, identifiant);
            glBufferData(GL_ARRAY_BUFFER, flottants, GL_STATIC_DRAW);
            this.tanpom.add(identifiant);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            identifiant = glGenBuffers();
            IntBuffer entiers = pile.mallocInt(indices.length);
            entiers.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, identifiant);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, entiers, GL_STATIC_DRAW);
            this.tanpom.add(identifiant);

            identifiant = glGenBuffers();
            flottants = pile.mallocFloat(textures.length);
            flottants.put(textures).flip();
            glBindBuffer(GL_ARRAY_BUFFER, identifiant);
            glBufferData(GL_ARRAY_BUFFER, flottants, GL_STATIC_DRAW);
            this.tanpom.add(identifiant);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        this.deplacement = Matrice.identitef(4, 4);
        this.transitions = new LinkedList<>();
        // TODO cas générale, ici fonctionne pour une forme à 4 sommets
        this.gravite = Matrice.translation(new float[]{
                -(positions[0] + positions[3] + positions[6] + positions[9]) / 4,
                -(positions[1] + positions[4] + positions[7] + positions[10]) / 4,
                -(positions[2] + positions[5] + positions[8] + positions[11]) / 4
        });
        this.contour = Matrice.identitef(4, positions.length / 3).insererParLignes((ligne, colonne, _) -> ligne % 4 == 3 ? 1f : contour[colonne * 3 + ligne]);
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
        tanpom.forEach(GL15::glDeleteBuffers);
        glDeleteVertexArrays(vecteurs);
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public void maj(Vision vision, EvenementSouris evenementSouris, EvenementAmorcages evenementAmorcagesCourant) {
        // Rien à faire
    }

    @Override
    public void produire(long ellipse, Vision vision) {
        if (!transitions.isEmpty()) {
            transitions.getFirst().lineariser(ellipse);
            transitions.removeIf(Transition::finie);
        }

        Optional<Integer> textureBase = Tisseur.obtenir(parent.identifiant(), Pseudo.PSEUDO_BASE);
        Optional<Integer> textureSurvole = Tisseur.obtenir(parent.identifiant(), Pseudo.PSEUDO_SURVOLE);
        Optional<Integer> textureActiver = Tisseur.obtenir(parent.identifiant(), Pseudo.PSEUDO_ACTIVER);

        Ombreur.configurer("deplacement", deplacement);
        Ombreur.configurer("echantillonneur", 0);
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

        Map<String, Programme> programmes = Ombreur.programmes(nom);
        if (programmes.containsKey(Pseudo.PSEUDO_ACTIVER) && active) {
            Ombreur.utiliser(programmes.get(Pseudo.PSEUDO_ACTIVER));
        } else if (programmes.containsKey(Pseudo.PSEUDO_SURVOLE) && survole) {
            Ombreur.utiliser(programmes.get(Pseudo.PSEUDO_SURVOLE));
        } else if (programmes.containsKey(Pseudo.PSEUDO_BASE)) {
            Ombreur.utiliser(programmes.get(Pseudo.PSEUDO_BASE));
        } else {
            Ombreur.utiliser(Ombreur.programmes(NOM_PAR_DEFAUT).get(Pseudo.DEFAUT));
        }

        glBindVertexArray(vecteurs);
        glDrawElements(GL_TRIANGLES, taille, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void deplacer(float[] position, long duree) {
        Float[] depart = transitions.isEmpty()
                ? new Float[]{deplacement.valeur(0, 3), deplacement.valeur(1, 3), deplacement.valeur(2, 3)}
                : transitions.getLast().arrive();
        Float[] arrivee = gravite.multiplication(Matrice.vecteur(position)).colonne(0);
        transitions.addLast(new Transition<>(deplacement, depart, arrivee, duree) {
            @Override
            protected Float[] multiplierParScalaire(Double scalaire, Float[] valeur) {
                return new Float[]{(float) (scalaire * valeur[0]), (float) (scalaire * valeur[1]), (float) (scalaire * valeur[2])};
            }

            @Override
            protected Float[] sommer(Float[] valeur1, Float[] valeur2) {
                return new Float[]{valeur1[0] + valeur2[0], valeur1[1] + valeur2[1], valeur1[2] + valeur2[2]};
            }

            @Override
            protected void appliquer(Matrice<Float> matrice, Float[] valeur) {
                matrice.modifierValeur(0, 3, valeur[0], (_, nouvelle) -> nouvelle);
                matrice.modifierValeur(1, 3, valeur[1], (_, nouvelle) -> nouvelle);
                matrice.modifierValeur(2, 3, valeur[2], (_, nouvelle) -> nouvelle);
            }
        });
    }

    public void preparer(Vision vision) {
        boite = new Boite(vision.matrice().multiplication(parent.transformation()).multiplication(deplacement).multiplication(contour));
    }
}
