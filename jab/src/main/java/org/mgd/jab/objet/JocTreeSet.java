package org.mgd.jab.objet;

import org.mgd.jab.dto.Dto;

import java.util.*;

/**
 * Implementations d'une {@link TreeSet} d'objet métier de type {@link Jo} qui permet notamment la gestion des parents
 * d'un objet métier.
 *
 * @param <T> le type des éléments de la collection
 * @author Maxime
 */
public class JocTreeSet<T extends Comparable<? super T>> extends JocAbstractSet<T, TreeSet<T>> implements NavigableSet<T> {
    public JocTreeSet(Jo<? extends Dto> contenant) {
        super(contenant);
        this.contenu = new TreeSet<>();
    }

    @Override
    public T lower(T t) {
        return this.contenu.lower(t);
    }

    @Override
    public T floor(T t) {
        return this.contenu.floor(t);
    }

    @Override
    public T ceiling(T t) {
        return this.contenu.ceiling(t);
    }

    @Override
    public T higher(T t) {
        return this.contenu.higher(t);
    }

    @Override
    public T pollFirst() {
        return this.contenu.pollFirst();
    }

    @Override
    public T pollLast() {
        return this.contenu.pollLast();
    }

    @Override
    public NavigableSet<T> descendingSet() {
        JocTreeSet<T> jocTreeSet = new JocTreeSet<>(this.contenant);
        jocTreeSet.addAll(this.contenu.descendingSet());
        return jocTreeSet;
    }

    @Override
    public Iterator<T> descendingIterator() {
        return this.contenu.descendingIterator();
    }

    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        JocTreeSet<T> jocTreeSet = new JocTreeSet<>(this.contenant);
        jocTreeSet.addAll(this.contenu.subSet(fromElement, fromInclusive, toElement, toInclusive));
        return jocTreeSet;
    }

    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        JocTreeSet<T> jocTreeSet = new JocTreeSet<>(this.contenant);
        jocTreeSet.addAll(this.contenu.headSet(toElement, inclusive));
        return jocTreeSet;
    }

    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {
        JocTreeSet<T> jocTreeSet = new JocTreeSet<>(this.contenant);
        jocTreeSet.addAll(this.contenu.tailSet(fromElement, inclusive));
        return jocTreeSet;
    }

    @Override
    public Comparator<? super T> comparator() {
        return this.contenu.comparator();
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        JocTreeSet<T> jocTreeSet = new JocTreeSet<>(this.contenant);
        jocTreeSet.addAll(this.contenu.subSet(fromElement, toElement));
        return jocTreeSet;
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        JocTreeSet<T> jocTreeSet = new JocTreeSet<>(this.contenant);
        jocTreeSet.addAll(this.contenu.headSet(toElement));
        return jocTreeSet;
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        JocTreeSet<T> jocTreeSet = new JocTreeSet<>(this.contenant);
        jocTreeSet.addAll(this.contenu.tailSet(fromElement));
        return jocTreeSet;
    }

    @Override
    public T first() {
        return this.contenu.first();
    }

    @Override
    public T last() {
        return this.contenu.last();
    }
}
