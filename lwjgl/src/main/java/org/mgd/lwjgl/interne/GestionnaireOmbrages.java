package org.mgd.lwjgl.interne;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Programme;
import org.mgd.lwjgl.exception.LwjglException;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL20.*;

// Nuanceur
public class GestionnaireOmbrages {
    private static final Map<String, Map<String, Programme>> programmesParObjetEtPseudo = new HashMap<>();
    private static final Map<String, Programme> programmesParNom = new HashMap<>();
    private static final Map<String, Integer> identifiantsParNom = new HashMap<>();
    private static final Map<String, Consumer<String>> specificationsParUniforme = new HashMap<>();

    private GestionnaireOmbrages() {
    }

    public static void ajouter(String nom, String pseudo, Programme programme) {
        programmesParObjetEtPseudo.computeIfAbsent(nom, _ -> new HashMap<>()).put(pseudo, programme);
        programmesParNom.put(Programme.nommer(nom, pseudo), programme);
    }

    public static void creer(Programme programme) throws LwjglException {
        int identifiant = glCreateProgram();
        if (identifiant == 0) {
            throw new LwjglException("Impossible de créer le programme d'ombrage.");
        }
        List<Integer> ombrages = new ArrayList<>(programme.cheminsOmbrages().size());
        try {
            for (Map.Entry<Integer, Path> entry : programme.cheminsOmbrages().entrySet()) {
                Integer type = entry.getKey();
                Path chemin = entry.getValue();
                ombrages.add(compiler(type, chemin));
                glAttachShader(identifiant, ombrages.getLast());
            }
            glLinkProgram(identifiant);
            if (glGetProgrami(identifiant, GL_LINK_STATUS) != GL_TRUE) {
                throw new LwjglException(glGetProgramInfoLog(identifiant));
            }
            Integer identifiantPrecedent = identifiantsParNom.put(programme.nom(), identifiant);
            if (identifiantPrecedent != null) {
                glDeleteProgram(identifiantPrecedent);
            }
        } catch (IOException e) {
            throw new LwjglException(e);
        } finally {
            ombrages.forEach(GL20::glDeleteShader);
            if (!identifiantsParNom.containsValue(identifiant)) {
                glDeleteProgram(identifiant);
            }
        }
    }

    public static void configurer(String nomUniforme, Matrice<Float> matrice) {
        specificationsParUniforme.put(nomUniforme, nom -> {
            int localisation = glGetUniformLocation(identifiantsParNom.get(nom), nomUniforme);
            if (localisation >= 0) {
                try (MemoryStack pile = MemoryStack.stackPush()) {
                    FloatBuffer flottants = pile.mallocFloat(matrice.nombreLignes() * matrice.nombreColonnes());
                    matrice.parcoursParColonnes((_, _, _, valeur) -> flottants.put(valeur));
                    flottants.flip();
                    glUniformMatrix4fv(localisation, false, flottants);
                }
            }
        });

    }

    public static void configurer(String nomUniforme, int valeur) {
        specificationsParUniforme.put(nomUniforme, nom -> {
            int localisation = glGetUniformLocation(identifiantsParNom.get(nom), nomUniforme);
            if (localisation >= 0) {
                glUniform1i(localisation, valeur);
            }
        });
    }

    public static void utiliser(Programme programme) {
        glUseProgram(identifiantsParNom.get(programme.nom()));
        specificationsParUniforme.values().forEach(specification -> specification.accept(programme.nom()));
    }

    public static void nettoyer() {
        identifiantsParNom.values().forEach(GL20::glDeleteProgram);
        identifiantsParNom.clear();
        specificationsParUniforme.clear();
    }

    public static Map<String, Programme> programmes(String nom) {
        return programmesParObjetEtPseudo.getOrDefault(nom, Collections.emptyMap());
    }

    public static Collection<Programme> programmes() {
        return programmesParNom.values();
    }

    private static int compiler(int type, Path fichier) throws IOException, LwjglException {
        String source = Files.readString(fichier);
        int ombrage = glCreateShader(type);
        if (ombrage == 0) {
            throw new LwjglException(MessageFormat.format("Erreur lors de la création d''un ombrage de type {0}.", type));
        }

        glShaderSource(ombrage, source);
        glCompileShader(ombrage);

        if (glGetShaderi(ombrage, GL_COMPILE_STATUS) != GL_TRUE) {
            try {
                throw new LwjglException(glGetShaderInfoLog(ombrage));
            } finally {
                GL20.glDeleteShader(ombrage);
            }
        }

        return ombrage;
    }
}
