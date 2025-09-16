package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("java:S2160")
public class Des extends Jo {
    public static final int QUANTITE_PAR_VALEUR = 10;

    private final Deque<Integer> valeurs = new LinkedList<>();
    private Integer maximum;
    private Integer valeur;

    public Deque<Integer> getValeurs() {
        return valeurs;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public Integer getValeur() {
        return valeur;
    }

    public void setValeur(Integer valeur) {
        this.valeur = valeur;
    }

    public void lancer() {
        maj();
        valeur = valeurs.remove();
    }

    public void exploser() {
        maj();
        valeur += valeurs.remove();
    }

    private void maj() {
        if (valeurs.isEmpty()) {
            Objects.requireNonNull(maximum);
            int quantite = maximum * QUANTITE_PAR_VALEUR;
            valeurs.addAll(IntStream.range(0, quantite)
                    .mapToObj(entier -> 1 + entier % maximum)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                        Collections.shuffle(collected);
                        return collected;
                    })));
        }
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Des des)) return false;
        return valeurs.equals(des.valeurs) && Objects.equals(maximum, des.maximum) && Objects.equals(valeur, des.valeur);
    }
}
