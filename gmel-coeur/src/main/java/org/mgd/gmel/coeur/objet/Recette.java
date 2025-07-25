package org.mgd.gmel.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;
import org.mgd.jab.objet.JocTreeSet;

import java.util.SortedSet;

/**
 * Objet métier représentant une recette de cuisine.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Recette extends Jo implements Comparable<Recette> {
    private final Joc<String> nom = new Joc<>(this);
    private final Joc<Integer> nombrePersonnes = new Joc<>(this);
    private final SortedSet<ProduitQuantifier> produitsQuantifier = new JocTreeSet<>(this);

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public Integer getNombrePersonnes() {
        return nombrePersonnes.get();
    }

    public void setNombrePersonnes(Integer nombrePersonnes) {
        this.nombrePersonnes.set(nombrePersonnes);
    }

    public void setNombrePersonnes(Number nombrePersonnes) {
        this.nombrePersonnes.set(nombrePersonnes.intValue());
    }

    public SortedSet<ProduitQuantifier> getProduitsQuantifier() {
        return produitsQuantifier;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Recette recette)) return false;
        return nom.idem(recette.nom)
                && nombrePersonnes.idem(recette.nombrePersonnes)
                && ((JocTreeSet<ProduitQuantifier>) produitsQuantifier).idem(recette.produitsQuantifier);
    }

    @Override
    public int compareTo(Recette recette) {
        return nom.get().compareToIgnoreCase(recette.nom.get());
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
