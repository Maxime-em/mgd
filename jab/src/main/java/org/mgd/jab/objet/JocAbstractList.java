package org.mgd.jab.objet;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * Classe abstraite à utiliser pour créer des implementations de {@link List} d'objet métier de type {@link Jo}
 * qui permet notamment la gestion des parents d'un objet métier.
 *
 * @param <T> le type des éléments de la collection
 * @param <C> le type de la collection
 * @author Maxime
 */
public abstract class JocAbstractList<T, C extends AbstractList<T>> extends JocAbstractCollection<T, C> implements List<T> {
    protected JocAbstractList(Jo contenant) {
        super(contenant);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        contenant.ajouterEnfants(c);
        if (contenu.addAll(index, c)) {
            contenant.sauvegarder();
            return true;
        }
        return false;
    }

    @Override
    public T get(int index) {
        return contenu.get(index);
    }

    @Override
    public T set(int index, T element) {
        contenant.ajouterEnfant(element);
        T resultat = contenu.set(index, element);
        // TODO mettre du code pour ne sauvegarder que si la collection à réellement changée
        contenant.sauvegarder();
        return resultat;
    }

    @Override
    public void add(int index, T element) {
        contenant.ajouterEnfant(element);
        contenu.add(index, element);
        // TODO mettre du code pour ne sauvegarder que si la collection à réellement changée
        contenant.sauvegarder();
    }

    @Override
    public T remove(int index) {
        contenant.enleverEnfant(get(index));
        T resultat = contenu.remove(index);
        // TODO mettre du code pour ne sauvegarder que si la collection à réellement changée
        contenant.sauvegarder();
        return resultat;
    }

    @Override
    public int indexOf(Object o) {
        return contenu.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return contenu.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return contenu.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return contenu.listIterator();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        JocArrayList<T> jocArrayList = new JocArrayList<>(contenant);
        jocArrayList.addAll(contenu.subList(fromIndex, toIndex));
        return jocArrayList;
    }
}
