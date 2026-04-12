package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.commun.Tabulable;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class TypeTransport extends Type implements Comparable<TypeTransport>, Tabulable {
    private final Set<TypeRegion> praticables = new TreeSet<>();
    private final Integer[] texture = new Integer[2];

    public Set<TypeRegion> getPraticables() {
        return praticables;
    }

    @Override
    public Integer ligne() {
        return texture[0];
    }

    @Override
    public void ligne(Integer ligne) {
        texture[0] = ligne;
    }

    @Override
    public Integer colonne() {
        return texture[1];
    }

    @Override
    public void colonne(Integer colonne) {
        texture[1] = colonne;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof TypeTransport type)) return false;
        return nom.equals(type.nom);
    }

    @Override
    public int compareTo(@NotNull TypeTransport type) {
        return Comparator.comparing(TypeTransport::getNom).compare(this, type);
    }
}
