package org.mgd.jab;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.connexion.Connectable;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.exception.JabException;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.source.Ad;
import org.mgd.jab.source.Af;
import org.mgd.utilitaire.Fichiers;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Facade à surcharger par le module métier pour charger un système de fichier JSON.
 *
 * @author Maxime
 */
public abstract class Jab implements Connectable {
    private static final Logger LOGGER = LogManager.getLogger(Jab.class);

    protected final Path base;
    protected final Properties proprietes = new Properties();
    protected final SortedMap<String, Jao<? extends Dto, ? extends Jo>> jaos = new TreeMap<>();
    protected final SortedMap<String, Ad<? extends Dto, ? extends Jo, ? extends Af<? extends Dto, ? extends Jo>>> ads = new TreeMap<>();
    private final JabSauvegarde sauvegarde;

    protected Jab(Path chemin) throws JabException {
        this.sauvegarde = JabSingletons.sauvegarde();

        if (chemin == null) {
            throw new JabException("Le chemin vers le fichier de configuration du Jab ne doit pas être nul.");
        }

        Path fichier = Files.isRegularFile(chemin) ? chemin : chemin.resolve("jab.properties");
        try (BufferedReader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            proprietes.load(lecteur);
            if (!proprietes.containsKey("jab.base")) {
                throw new JabException("Le fichier de configuration du Jab doit contenir le chemin vers la base via la propriété jab.base.");
            }
            base = Paths.get(proprietes.getProperty("jab.base"));
            configurerSauvegarde();
            remplirJaos();
            remplirAds();
        } catch (IOException e) {
            throw new JabException(MessageFormat.format("Impossible de lire le fichier de configuration du Jab avec le chemin {0}.", chemin), e);
        }
    }

    public void disposer() {
        sauvegarde.ecrire();
        sauvegarde.disposer();
    }

    private void configurerSauvegarde() {
        sauvegarde.setNombreThreads(Integer.parseInt(proprietes.getProperty("jab.sauvegarde.nombre.threads", "100")));
        sauvegarde.setAsynchrone(Boolean.parseBoolean(proprietes.getProperty("jab.sauvegarde.asynchrone", "true")));
        sauvegarde.setDelai(Long.parseLong(proprietes.getProperty("jab.sauvegarde.delai", "60000")));
        sauvegarde.reinitialiser();
    }

    private void remplirJaos() throws JabException {
        try {
            if (proprietes.containsKey("jab.persistences")) {
                for (String persistence : proprietes.getProperty("jab.persistences").split(",")) {
                    String[] chaines = persistence.split(":");
                    jaos.put(chaines[0], (Jao<? extends Dto, ? extends Jo>) Class.forName(chaines[1]).getConstructor().newInstance());
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new JabException(e);
        }
    }

    private void remplirAds() throws JabException {
        try {
            if (proprietes.containsKey("jab.sources")) {
                for (String ad : proprietes.getProperty("jab.sources").split(",")) {
                    String[] chaines = ad.split(":");
                    String pattern = chaines[0];
                    if (pattern == null || pattern.isBlank()) {
                        throw new JabException("Les noms des sources ne doivent pas être vide.");
                    }
                    for (Path dossier : Fichiers.rechercherDossiers(base, pattern)) {
                        String nom = dossier.getFileName().toString();
                        if (ads.containsKey(nom)) {
                            LOGGER.warn("Le nom {} de l'accès au dossier {} existe déjà pour le dossier {}.", nom, dossier, ads.get(nom));
                        }
                        ads.put(nom, (Ad<? extends Dto, ? extends Jo, ? extends Af<? extends Dto, ? extends Jo>>) Class.forName(chaines[1]).getDeclaredConstructor(Path.class).newInstance(dossier));
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | IOException e) {
            throw new JabException(e);
        }
    }
}
