package org.mgd.jab;

import org.mgd.jab.dto.Dto;
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
    private final SortedMap<UUID, Jao<? extends Dto, ? extends Jo>> jaos = new TreeMap<>();

    public SortedMap<Path, UUID> getGermes() {
        return germes;
    }

    public SortedMap<UUID, Path> getFichiers() {
        return fichiers;
    }

    public Path getFichier(UUID identifiant) {
        return fichiers.get(identifiant);
    }

    @SuppressWarnings("unchecked")
    public <D extends Dto, O extends Jo> Jao<D, O> getJao(UUID identifiant) {
        return (Jao<D, O>) jaos.get(identifiant);
    }

    public boolean verifier(UUID identifiant) {
        return fichiers.containsKey(identifiant) && jaos.containsKey(identifiant);
    }

    public void reinitialiser() {
        germes.clear();
        fichiers.clear();
        jaos.clear();
    }

    public <D extends Dto, O extends Jo, J extends Jao<D, O>> void ajouter(Path fichier, UUID identifiant, J jao) {
        germes.put(fichier.toAbsolutePath(), identifiant);
        fichiers.put(identifiant, fichier.toAbsolutePath());
        jaos.put(identifiant, jao);
    }
}
