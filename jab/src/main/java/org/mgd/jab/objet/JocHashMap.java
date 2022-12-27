package org.mgd.jab.objet;

import org.mgd.jab.dto.Dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementations d'une {@link HashMap} d'objet métier de type {@link Jo} qui permet notamment la gestion des parents
 * d'un objet métier.
 *
 * @param <K> le type des clés utilisées par ce tableau associatif
 * @param <V> le type des valeurs associées
 * @author Maxime
 */
public class JocHashMap<K, V> extends JocAbstractMap<K, V, HashMap<K, V>> implements Map<K, V> {
    public JocHashMap(Jo<? extends Dto> contenant) {
        super(contenant);
        this.contenu = new HashMap<>();
    }
}
