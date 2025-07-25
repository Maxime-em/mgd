package org.mgd.gmel.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;

/**
 * Objet métier représentant un produit qui peut être mis à disposition par une épicerie.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Produit extends Jo implements Comparable<Produit> {
    private final Joc<String> nom = new Joc<>(this);

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Produit produit)) return false;
        return nom.idem(produit.nom);
    }

    @Override
    public int compareTo(Produit produit) {
        return nom.get().compareToIgnoreCase(produit.nom.get());
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
