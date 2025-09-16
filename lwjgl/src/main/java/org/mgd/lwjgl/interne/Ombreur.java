package org.mgd.lwjgl.interne;

import org.lwjgl.system.MemoryStack;
import org.mgd.commun.Matrice;
import org.mgd.lwjgl.exception.LwjglException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL20.*;

// Nuanceur
public class Ombreur {
    private static int programme;
    private static int vectoriel;
    private static int fragment;
    private static Map<String, Integer> uniformes;

    private Ombreur() {
    }

    public static void creer() throws LwjglException {
        programme = glCreateProgram();
        if (programme == 0) {
            throw new LwjglException("Impossible de créer le programme d'ombrage.");
        }
        uniformes = new HashMap<>();
    }

    public static void compiler() throws IOException, LwjglException, URISyntaxException {
        vectoriel = compiler(GL_VERTEX_SHADER, Path.of(Objects.requireNonNull(Ombreur.class.getResource("basique.vert")).toURI()));
        fragment = compiler(GL_FRAGMENT_SHADER, Path.of(Objects.requireNonNull(Ombreur.class.getResource("basique.frag")).toURI()));
    }

    public static void charger() throws LwjglException {
        glLinkProgram(programme);

        if (glGetProgrami(programme, GL_LINK_STATUS) != GL_TRUE) {
            throw new LwjglException(glGetProgramInfoLog(programme));
        }

        glDetachShader(programme, vectoriel);
        glDetachShader(programme, fragment);

        glDeleteShader(vectoriel);
        glDeleteShader(fragment);

        obtenirLocalisation("projection");
        obtenirLocalisation("vision");
        obtenirLocalisation("transformation");
        obtenirLocalisation("colorisation");
        obtenirLocalisation("fixe");
        obtenirLocalisation("echantillonneur");
    }

    private static void obtenirLocalisation(String nom) throws LwjglException {
        int localisation = glGetUniformLocation(programme, nom);
        if (localisation < 0) {
            throw new LwjglException(MessageFormat.format("Variable uniforme {0} introuvable.", localisation));
        }
        uniformes.put(nom, localisation);
    }

    public static void specifier(String nom, Matrice<Float> matrice) {
        try (MemoryStack pile = MemoryStack.stackPush()) {
            FloatBuffer flottants = pile.mallocFloat(matrice.nombreLignes() * matrice.nombreColonnes());
            matrice.parcoursParColonnes((ligne, colonne, index, valeur) -> flottants.put(valeur));
            flottants.flip();
            glUniformMatrix4fv(uniformes.get(nom), false, flottants);
        }
    }

    public static void specifier(String nom, int valeur) {
        glUniform1i(uniformes.get(nom), valeur);
    }

    public static void utiliser() {
        glUseProgram(programme);
    }

    public static void perdre() {
        glUseProgram(0);
    }

    public static void nettoyer() {
        glDeleteProgram(programme);
    }

    private static int compiler(int type, Path fichier) throws IOException, LwjglException {
        int ombrage = glCreateShader(type);
        if (ombrage == 0) {
            throw new LwjglException(MessageFormat.format("Erreur lors de la création d''un ombrage de type {0}.", type));
        }

        glShaderSource(ombrage, Files.readString(fichier));
        glCompileShader(ombrage);

        if (glGetShaderi(ombrage, GL_COMPILE_STATUS) != GL_TRUE) {
            throw new LwjglException(glGetShaderInfoLog(ombrage));
        }

        glAttachShader(programme, ombrage);

        return ombrage;
    }
}
