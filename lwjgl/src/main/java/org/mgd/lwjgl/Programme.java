package org.mgd.lwjgl;

import org.mgd.lwjgl.interne.Ombreur;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public record Programme(String nom, Map<Integer, Path> cheminsOmbrages, Collection<String> nomsUniformes) {
    public static final String NOM_DOSSIER_OMBRAGES = "ombrages";
    public static final String NOM_PAR_DEFAUT = "programme";

    public static String nommer(String nom, String pseudo) {
        return MessageFormat.format("{0}-{1}", nom, pseudo);
    }

    public static void nouveau(String nom, String pseudo, Path dossier, String[] types, String[] uniformes) {
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
        Programme programme = new Programme(nommer(nom, pseudo), cheminsOmbrages, Stream.of(uniformes).toList());
        Ombreur.ajouter(nom, pseudo, programme);
    }
}
