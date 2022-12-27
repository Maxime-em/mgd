package org.mgd.jab.objet;

import org.mgd.jab.dto.Dto;
import org.mgd.jab.utilitaire.Jos;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * Classe abstraite à utiliser pour créer des implementations de {@link Collection} d'objet métier de type {@link Jo}
 * qui permet notamment la gestion des parents d'un objet métier.
 *
 * @param <T> le type des éléments de la collection
 * @param <C> le type de la collection
 * @author Maxime
 */
public abstract class JocAbstractCollection<T, C extends AbstractCollection<T>> extends Joc<C> implements Collection<T> {
    protected JocAbstractCollection(Jo<? extends Dto> contenant) {
        super(contenant);
    }

    @Override
    public int size() {
        return this.contenu.size();
    }

    @Override
    public boolean isEmpty() {
        return this.contenu.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.contenu.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.contenu.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.contenu.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.contenu.toArray(a);
    }

    @Override
    public boolean add(T t) {
        this.contenant.ajouterEnfant(t);
        boolean resultat = contenu.add(t);
        this.contenant.sauvegarder();
        return resultat;
    }

    @Override
    public boolean remove(Object o) {
        this.contenant.enleverEnfant(o);
        boolean resultat = contenu.remove(o);
        this.contenant.sauvegarder();
        return resultat;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.contenu.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        this.contenant.ajouterEnfants(c);
        boolean resultat = contenu.addAll(c);
        this.contenant.sauvegarder();
        return resultat;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.contenant.removeEnfants(c);
        boolean resultat = contenu.removeAll(c);
        this.contenant.sauvegarder();
        return resultat;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        this.contenant.removeEnfants(this.contenu.stream().filter(t -> !c.contains(t)).toList());
        boolean resultat = this.contenu.retainAll(c);
        this.contenant.sauvegarder();
        return resultat;
    }

    @Override
    public void clear() {
        this.contenant.removeEnfants(this.contenu);
        this.contenu.clear();
        this.contenant.sauvegarder();
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof JocAbstractCollection<?, ?> collection)) return false;
        if (this.contenu == collection.contenu) return true;
        if (this.contenu == null || collection.contenu == null) return false;
        if (this.contenu.size() != collection.contenu.size()) return false;
        return this.contenu.stream().allMatch(element1 -> collection.contenu.stream().anyMatch(element2 -> Jos.idem(element1, element2)));
    }
}
