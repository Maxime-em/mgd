package org.mgd.jab.objet;

import org.mgd.jab.utilitaire.Jos;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Classe abstraite à utiliser pour créer des implementations de {@link Map} d'objet métier de type {@link Jo}
 * qui permet notamment la gestion des parents d'un objet métier.
 *
 * @param <K> le type des clés utilisées par ce tableau associatif
 * @param <V> le type des valeurs associées
 * @param <C> le type de tableau associatif
 * @author Maxime
 */
public abstract class JocAbstractMap<K, V, C extends AbstractMap<K, V>> extends Joc<C> implements Map<K, V> {
    protected JocAbstractMap(Jo contenant) {
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
    public boolean containsKey(Object key) {
        return this.contenu.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.contenu.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.contenu.get(key);
    }

    @Override
    public V put(K key, V value) {
        this.contenant.ajouterEnfant(value);
        V result = contenu.put(key, value);
        this.contenant.sauvegarder();
        return result;
    }

    @Override
    public V remove(Object key) {
        this.contenant.enleverEnfant(get(key));
        V result = contenu.remove(key);
        this.contenant.sauvegarder();
        return result;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.contenant.ajouterEnfants(m.values());
        contenu.putAll(m);
        this.contenant.sauvegarder();
    }

    @Override
    public void clear() {
        this.contenant.ajouterEnfants(this.contenu.values());
        this.contenu.clear();
        this.contenant.sauvegarder();
    }

    @Override
    public Set<K> keySet() {
        return this.contenu.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.contenu.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.contenu.entrySet();
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof JocAbstractMap<?, ?, ?> tableau)) return false;
        if (tableau.size() != size()) return false;
        return entrySet().stream().allMatch(element1 -> tableau.entrySet().stream().anyMatch(element2 -> Jos.idem(element1.getKey(), element2.getKey()) && Jos.idem(element1.getValue(), element2.getValue())));
    }
}
