package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;

import java.util.Comparator;

/**
 * Objet métier représentant une quantité qui sera associée à un produit pour faire partie d'une recette de cuisines.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Quantite extends Jo implements Comparable<Quantite> {
    private final Joc<Long> valeur = new Joc<>(this);
    private final Joc<Mesure> mesure = new Joc<>(this);

    public Long getValeur() {
        return valeur.get();
    }

    public void setValeur(Long valeur) {
        this.valeur.set(valeur);
    }

    public Mesure getMesure() {
        return mesure.get();
    }

    public void setMesure(Mesure mesure) {
        this.mesure.set(mesure);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Quantite quantite)) return false;
        return valeur.idem(quantite.valeur) && mesure.idem(quantite.mesure);
    }

    public String getUnite() {
        return mesure.get().getUnite(Math.abs(valeur.get()) < 2);
    }

    @Override
    public int compareTo(Quantite quantite) {
        return Comparator.comparing(Quantite::getMesure).thenComparing(Quantite::getValeur).compare(this, quantite);
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
