package org.mgd.lwjgl;

import org.mgd.lwjgl.interne.GestionnaireOmbrages;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public record Programme(String nom, Map<Integer, Path> cheminsOmbrages) {
    public static final String NOM_DOSSIER_OMBRAGES = "ombrages";
    public static final String NOM_OMBRAGE_PAR_DEFAUT = "defaut";

    public static String nommer(String nom, String pseudo) {
        return MessageFormat.format("{0}-{1}", nom, pseudo);
    }

    public static void nouveau(String nom, String pseudo, Path dossier, String[] types) {
        Map<Integer, Path> cheminsOmbrages = Stream.of(types).collect(Collectors.toMap(
                type -> switch (type) {
                    case "vecteur" -> GL_VERTEX_SHADER;
                    case "fragment" -> GL_FRAGMENT_SHADER;
                    default -> 0;
                },
                type -> switch (type) {
                    case "vecteur" -> dossier.resolve(NOM_DOSSIER_OMBRAGES, nom, pseudo, pseudo + ".vert");
                    case "fragment" -> dossier.resolve(NOM_DOSSIER_OMBRAGES, nom, pseudo, pseudo + ".frag");
                    default -> dossier.resolve(NOM_DOSSIER_OMBRAGES, nom, pseudo, pseudo + ".inconnu");
                }
        ));
        for (Path chemin : cheminsOmbrages.values()) {
            if (!Files.exists(chemin)) {
                throw new IllegalArgumentException(MessageFormat.format("Le fichier d''ombrage {0} n''existe pas.", chemin));
            }
        }
        Programme programme = new Programme(nommer(nom, pseudo), cheminsOmbrages);
        GestionnaireOmbrages.ajouter(nom, pseudo, programme);
    }
}
