package org.mgd.gmel.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;
import org.mgd.temps.LocalRepas;

import java.util.Comparator;

/**
 * Objet métier représentant une période, celle-ci début à {@code repas} et dure {@code taille} demi-journées.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Periode extends Jo implements Comparable<Periode> {
    private final Joc<LocalRepas> repas = new Joc<>(this);
    private final Joc<Integer> taille = new Joc<>(this);

    public LocalRepas getRepas() {
        return repas.get();
    }

    public void setRepas(LocalRepas repas) {
        this.repas.set(repas);
    }

    public Integer getTaille() {
        return taille.get();
    }

    public void setTaille(Integer taille) {
        this.taille.set(taille);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Periode periode)) return false;
        return repas.idem(periode.repas) && taille.idem(periode.taille);
    }

    @Override
    public int compareTo(Periode periode) {
        return Comparator.comparing(Periode::getRepas).thenComparing(Periode::getTaille).compare(this, periode);
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
