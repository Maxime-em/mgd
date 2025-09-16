package org.mgd.lwjgl.forme;

import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryStack;
import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.PseudoTisseur;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.element.Element;
import org.mgd.lwjgl.interne.Ombreur;
import org.mgd.lwjgl.interne.Tisseur;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.lwjgl.opengl.GL30.*;

public abstract class Forme {
    protected final Element<?> parent;
    protected final Boite boite;
    protected final boolean fixe;
    private final Colorisation colorisation;
    private final int taille;
    private final int vecteurs;
    private final Set<Integer> tanpom = new HashSet<>();
    protected boolean survole;
    protected boolean active;

    protected Forme(Element<?> parent, float[] positions, float[] textures, int[] indices, boolean fixe, Colorisation colorisation) {
        this.parent = parent;
        this.fixe = fixe;
        this.colorisation = colorisation;
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
            glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }

        this.boite = new Boite(positions);
    }

    public abstract boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris, Matrice<Float> transformation);

    public void desurvoler() {
        survole = false;
    }

    public void basculer() {
            active = !active;
    }

    public void deactiver() {
        active = false;
    }

    public void produire() {
        Optional<Integer> textureBase = Tisseur.obtenir(parent.identifiant(), PseudoTisseur.PSEUDO_BASE);
        Optional<Integer> textureSurvole = Tisseur.obtenir(parent.identifiant(), PseudoTisseur.PSEUDO_SURVOLE);
        Optional<Integer> textureActiver = Tisseur.obtenir(parent.identifiant(), PseudoTisseur.PSEUDO_ACTIVER);
        Ombreur.specifier("colorisation", colorisation.ordinal());
        Ombreur.specifier("fixe", fixe ? 1 : 0);
        Ombreur.specifier("echantillonneur", 0);
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

        glBindVertexArray(vecteurs);
        glDrawElements(GL_TRIANGLES, taille, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void nettoyer() {
        tanpom.forEach(GL15::glDeleteBuffers);
        glDeleteVertexArrays(vecteurs);
    }

    public boolean survole() {
        return survole;
    }

    public boolean active() {
        return active;
    }

    public float minimumz() {
        return boite.minimumz();
    }
}
