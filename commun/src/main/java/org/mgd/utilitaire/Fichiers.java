package org.mgd.utilitaire;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Fichiers {
    private Fichiers() {
    }

    public static List<Path> rechercher(Path racine, String pattern, Predicate<Path> filtrage) throws IOException {
        List<Path> dossiers = new ArrayList<>();
        try (DirectoryStream<Path> flux = Files.newDirectoryStream(racine, pattern)) {
            for (Path chemin : flux) {
                if (filtrage.test(chemin)) {
                    dossiers.add(chemin);
                }
            }
            return dossiers;
        }
    }

    public static List<Path> rechercherDossiers(Path racine, String pattern) throws IOException {
        return rechercher(racine, pattern, Files::isDirectory);
    }

    public static List<Path> rechercherFichiers(Path racine, String pattern) throws IOException {
        return rechercher(racine, pattern, Files::isRegularFile);
    }
}
