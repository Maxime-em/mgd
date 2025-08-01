package org.mgd.jab.objet;

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
    protected JocAbstractCollection(Jo contenant) {
        super(contenant);
    }

    @Override
    public int size() {
        return contenu.size();
    }

    @Override
    public boolean isEmpty() {
        return contenu.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return contenu.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return contenu.iterator();
    }

    @Override
    public Object[] toArray() {
        return contenu.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return contenu.toArray(a);
    }

    @Override
    public boolean add(T t) {
        contenant.ajouterEnfant(t);
        if (contenu.add(t)) {
            contenant.sauvegarder();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        contenant.enleverEnfant(o);
        if (contenu.remove(o)) {
            contenant.sauvegarder();
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return contenu.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        contenant.ajouterEnfants(c);
        if (contenu.addAll(c)) {
            contenant.sauvegarder();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        contenant.removeEnfants(c);
        if (contenu.removeAll(c)) {
            contenant.sauvegarder();
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        contenant.removeEnfants(contenu.stream().filter(t -> !c.contains(t)).toList());
        if (contenu.retainAll(c)) {
            contenant.sauvegarder();
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        contenant.removeEnfants(contenu);
        contenu.clear();
        // TODO mettre du code pour ne sauvegarder que si la collection à réellement changée
        contenant.sauvegarder();
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof JocAbstractCollection<?, ?> collection)) return false;
        if (contenu == collection.contenu) return true;
        if (contenu == null || collection.contenu == null) return false;
        if (contenu.size() != collection.contenu.size()) return false;
        return contenu.stream().allMatch(element1 -> collection.contenu.stream().anyMatch(element2 -> Jos.idem(element1, element2)));
    }
}
