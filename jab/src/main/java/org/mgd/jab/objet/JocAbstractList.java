package org.mgd.jab.objet;

import org.mgd.jab.dto.Dto;

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
    protected JocAbstractList(Jo<? extends Dto> contenant) {
        super(contenant);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        this.contenant.ajouterEnfants(c);
        boolean resultat = contenu.addAll(index, c);
        this.contenant.sauvegarder();
        return resultat;
    }

    @Override
    public T get(int index) {
        return this.contenu.get(index);
    }

    @Override
    public T set(int index, T element) {
        this.contenant.ajouterEnfant(element);
        T resultat = this.contenu.set(index, element);
        this.contenant.sauvegarder();
        return resultat;
    }

    @Override
    public void add(int index, T element) {
        this.contenant.ajouterEnfant(element);
        this.contenu.add(index, element);
        this.contenant.sauvegarder();
    }

    @Override
    public T remove(int index) {
        this.contenant.enleverEnfant(get(index));
        T resultat = this.contenu.remove(index);
        this.contenant.sauvegarder();
        return resultat;
    }

    @Override
    public int indexOf(Object o) {
        return this.contenu.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.contenu.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.contenu.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return this.contenu.listIterator();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        JocArrayList<T> jocArrayList = new JocArrayList<>(this.contenant);
        jocArrayList.addAll(this.contenu.subList(fromIndex, toIndex));
        return jocArrayList;
    }
}
