package org.mgd.jab.objet;

import org.mgd.jab.JabCreation;
import org.mgd.jab.JabSauvegarde;
import org.mgd.jab.JabSingletons;
import org.mgd.jab.persistence.Jao;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classe de base des objets métiers utilisés par les {@link Jao}.
 * Un objet métier contient au moins un identifiant unique et une liste d'objets parents.
 * Ces objets métiers sont créés par les {@link Jao} à partir d'un fichier JSON.
 * Un parent d'un objet métier est un autre objet métier qui contient l'objet dans un champ de type {@link Joc}
 * ou dans une collection de type {@link JocArrayList} ou {@link JocTreeSet}.
 *
 * @author Maxime
 */
public abstract class Jo {
    private final SortedSet<Jo> parents;
    private final SortedSet<Jo> enfants;
    private final JabSauvegarde sauvegarde;
    private final JabCreation creation;
    private UUID identifiant;
    private boolean detache;

    protected Jo() {
        this.parents = new TreeSet<>(Comparator.comparing(objet -> objet.identifiant));
        this.enfants = new TreeSet<>(Comparator.comparing(objet -> objet.identifiant));
        this.sauvegarde = JabSingletons.sauvegarde();
        this.creation = JabSingletons.creation();
        this.detache = true;
    }

    public abstract boolean idem(Object objet);

    protected <T> void ajouterEnfant(T enfant) {
        if (!Objects.isNull(enfant) && enfant instanceof Jo jo) {
            jo.parents.add(this);
            enfants.add(jo);
        }
    }

    protected <T> void enleverEnfant(T enfant) {
        if (!Objects.isNull(enfant) && enfant instanceof Jo jo) {
            jo.parents.remove(this);
            enfants.remove(jo);
        }
    }

    protected <T> void ajouterEnfants(Collection<T> enfants) {
        enfants.forEach(this::ajouterEnfant);
    }

    public boolean estEnfantDe(Jo parent) {
        return parents.contains(parent);
    }

    public void ajouterParent(Jo parent) {
        parents.add(parent);
        parent.enfants.add(this);
    }

    protected <T> void removeEnfants(Collection<T> enfants) {
        enfants.forEach(this::enleverEnfant);
    }

    public boolean estParentDe(Jo enfant) {
        return enfants.contains(enfant);
    }

    @SuppressWarnings("unchecked")
    public <Q extends Jo> Set<Q> racines(Class<Q> classe) {
        Stream<Q> racines = parents.stream().flatMap(parent -> parent.racines(classe).stream());
        return classe.isInstance(this) && creation.getFichiers().containsKey(identifiant)
                ? Stream.concat(Stream.of((Q) this), racines).collect(Collectors.toSet())
                : racines.collect(Collectors.toSet());
    }

    public UUID getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(UUID identifiant) {
        this.identifiant = identifiant;
    }

    public void sauvegarder() {
        if (!detache) {
            sauvegarde.demarrer(this);
            parents.forEach(Jo::sauvegarder);
        }
    }

    public void setDetache(boolean detache) {
        this.detache = detache;
        this.enfants.forEach(jo -> jo.setDetache(detache));
    }

    public boolean estDetache() {
        return detache;
    }

    @Override
    public boolean equals(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Jo jo)) return false;
        return Objects.equals(identifiant, jo.identifiant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifiant);
    }
}
