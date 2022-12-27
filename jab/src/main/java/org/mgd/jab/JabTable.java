package org.mgd.jab;

import org.mgd.jab.dto.Dto;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Classe représentant une table de données d'un type {@link O}.
 *
 * @param <O> Type des éléments de la table
 * @author Maxime
 */
public class JabTable<D extends Dto, O extends Jo<D>> {
    private final SortedSet<O> objets = new TreeSet<>(Comparator.comparing(Jo::getIdentifiant));

    public void referencer(O objet) {
        objets.add(objet);
    }

    public SortedSet<O> selectionner() {
        return objets;
    }

    public O selectionner(UUID identifiant) {
        return objets.stream().filter(objet -> objet.getIdentifiant().equals(identifiant)).findFirst().orElseThrow();
    }

    public boolean existe(UUID identifiant) {
        return objets.stream().anyMatch(objet -> objet.getIdentifiant().equals(identifiant));
    }
}
