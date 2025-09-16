package org.mgd.lwjgl.interne;

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

public class Tisseur {
    private static final Map<String, Integer> textures = new HashMap<>();

    private Tisseur() {
    }

    public static void compiler(String identifiant, Map<String, Path> fichiers) throws LwjglException {
        try (MemoryStack pile = MemoryStack.stackPush()) {
            for (Map.Entry<String, Path> element : fichiers.entrySet()) {
                textures.put(nom(identifiant, element.getKey()), creer(pile, element.getValue()));
            }
        }
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

        int identifiant = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, identifiant);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, largeur.get(), hauteur.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, bytes);
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(bytes);

        return identifiant;
    }

    private static String nom(String identifiant, String pseudo) {
        return String.join("@", identifiant, pseudo);
    }
}
