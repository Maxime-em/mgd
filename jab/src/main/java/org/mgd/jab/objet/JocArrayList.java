package org.mgd.jab.objet;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementations d'une {@link ArrayList} d'objet métier de type {@link Jo} qui permet notamment la gestion des parents
 * d'un objet métier.
 *
 * @param <T> le type des éléments de la collection
 * @author Maxime
 */
public class JocArrayList<T> extends JocAbstractList<T, ArrayList<T>> implements List<T> {
    public JocArrayList(Jo contenant) {
        super(contenant);
        contenu = new ArrayList<>();
    }
}
