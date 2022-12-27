package org.mgd.jab;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.Jao;

import java.nio.file.Path;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Classe utilisée instanciée par {@link JabSingletons} et utilisée par {@link Jao} pour conserver des données des
 * différents chargements d'objets métiers de type {@link Jo}.
 *
 * @author Maxime
 */
public class JabCreation {
    private final SortedMap<Path, UUID> germes = new TreeMap<>();
    private final SortedMap<UUID, Path> fichiers = new TreeMap<>();

    public SortedMap<Path, UUID> getGermes() {
        return germes;
    }

    public SortedMap<UUID, Path> getFichiers() {
        return fichiers;
    }

    public void reinitialiser() {
        germes.clear();
        fichiers.clear();
    }

    public void ajouter(Path fichier, UUID identifiant) {
        germes.put(fichier, identifiant);
        fichiers.put(identifiant, fichier);
    }
}
