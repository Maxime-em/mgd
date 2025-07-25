package org.mgd.gmel.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.JocTreeSet;

import java.util.SortedSet;

/**
 * Objet métier représentant un agenda des menus de la semaine.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Agenda extends Jo {
    private final SortedSet<Menu> menus = new JocTreeSet<>(this);

    public SortedSet<Menu> getMenus() {
        return menus;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Agenda agenda)) return false;
        return ((JocTreeSet<Menu>) menus).idem(agenda.menus);
    }
}
