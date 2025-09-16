package org.mgd.utilitaire;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
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

    public static <T> void parcourir(Path fichier, BiFunction<Integer, Integer, T> obtenir, BiConsumer<T, String> action) {
        try {
            AtomicInteger ligne = new AtomicInteger(0);
            Files.readAllLines(fichier)
                    .forEach(line -> {
                        AtomicInteger colonne = new AtomicInteger(0);
                        Arrays.stream(line.split("\t")).forEach(codes -> {
                            action.accept(obtenir.apply(ligne.get(), colonne.get()), codes);
                            colonne.incrementAndGet();
                        });
                        ligne.incrementAndGet();
                    });
        } catch (IOException e) {
            LOGGER.error("Impossible de gérer les codes pour le fichier {}.", fichier, e);
        }
    }
}
