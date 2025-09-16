package org.mgd.lwjgl.interne;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.mgd.lwjgl.exception.LwjglException;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class ChargeurTextures {
    private static final Map<String, Integer> textures = new HashMap<>();

    private ChargeurTextures() {
    }

    public static void compiler(String identifiant, Map<String, Path> fichiers) throws LwjglException {
        try (MemoryStack pile = MemoryStack.stackPush()) {
            for (Map.Entry<String, Path> element : fichiers.entrySet()) {
                String cle = nom(identifiant, element.getKey());
                int nouvelleTexture = creer(pile, element.getValue());
                Integer texturePrecedente = textures.put(cle, nouvelleTexture);
                if (texturePrecedente != null) {
                    glDeleteTextures(texturePrecedente);
                }
            }
        }
    }

    public static void nettoyer() {
        textures.values().forEach(GL11::glDeleteTextures);
        textures.clear();
    }

    public static Optional<Integer> obtenir(String identifiant, String pseudo) {
        return Optional.ofNullable(textures.get(nom(identifiant, pseudo)));
    }

    private static int creer(MemoryStack pile, Path fichier) throws LwjglException {
        IntBuffer largeur = pile.mallocInt(1);
        IntBuffer hauteur = pile.mallocInt(1);
        IntBuffer canaux = pile.mallocInt(1);

        ByteBuffer bytes = stbi_load(fichier.toString(), largeur, hauteur, canaux, 4);
        if (bytes == null) {
            throw new LwjglException(MessageFormat.format("L''image {0} est impossible à charger. {1}", fichier, stbi_failure_reason()));
        }

        int identifiant = 0;
        boolean creee = false;
        try {
            identifiant = glGenTextures();
            if (identifiant == 0) {
                throw new LwjglException("Impossible de créer la texture OpenGL.");
            }
            glBindTexture(GL_TEXTURE_2D, identifiant);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, largeur.get(), hauteur.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, bytes);
            glGenerateMipmap(GL_TEXTURE_2D);
            creee = true;
            return identifiant;
        } finally {
            stbi_image_free(bytes);
            if (!creee && identifiant != 0) {
                glDeleteTextures(identifiant);
            }
        }
    }

    private static String nom(String identifiant, String pseudo) {
        return String.join("@", identifiant, pseudo);
    }
}
