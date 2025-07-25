package org.mgd.gmel.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;
import org.mgd.jab.objet.JocTreeSet;

import java.util.Comparator;
import java.util.SortedSet;

/**
 * Objet métier représentant le menu de la semaine.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Menu extends Jo implements Comparable<Menu> {
    private final Joc<Integer> annee = new Joc<>(this);
    private final Joc<Integer> semaine = new Joc<>(this);
    private final SortedSet<Formule> formules = new JocTreeSet<>(this);

    public Integer getAnnee() {
        return annee.get();
    }

    public void setAnnee(Integer annee) {
        this.annee.set(annee);
    }

    public Integer getSemaine() {
        return semaine.get();
    }

    public void setSemaine(Integer semaine) {
        this.semaine.set(semaine);
    }

    public SortedSet<Formule> getFormules() {
        return formules;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Menu menu)) return false;
        return annee.idem(menu.annee)
                && semaine.idem(menu.semaine)
                && ((JocTreeSet<Formule>) formules).idem(menu.formules);
    }

    @Override
    public int compareTo(Menu menu) {
        return Comparator.comparing(Menu::getAnnee).thenComparing(Menu::getSemaine).thenComparing(Jo::getIdentifiant).compare(this, menu);
    }

    @Override
    public boolean equals(Object objet) {
        return super.equals(objet);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
