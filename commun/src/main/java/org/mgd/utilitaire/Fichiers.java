package org.mgd.utilitaire;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.commun.BiConsommateur;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Fichiers {
    private static final Logger LOGGER = LogManager.getLogger(Fichiers.class);

    private Fichiers() {
        throw new IllegalStateException("Classe utilitaire.");
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

    public static <T> void parcourir(Path fichier, BiFunction<Integer, Integer, T> obtenir, BiConsommateur<T, String> action) throws IOException {
        AtomicInteger ligne = new AtomicInteger(0);
        Files.readAllLines(fichier)
                .forEach(line -> {
                    AtomicInteger colonne = new AtomicInteger(0);
                    Arrays.stream(line.split("\t"))
                            .forEach(codes -> {
                                try {
                                    action.accepter(obtenir.apply(ligne.get(), colonne.get()), codes);
                                    colonne.incrementAndGet();
                                } catch (Exception e) {
                                    LOGGER.error("Impossible de gérer les codes {} au coordonnée {},{} pour le fichier {}.", codes, ligne, colonne, fichier, e);
                                }
                            });
                    ligne.incrementAndGet();
                });
    }
}
