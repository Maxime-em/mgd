package org.mgd.commun;

@FunctionalInterface
public interface MatriceIteration<T> {
    void recevoir(int ligne, int colonne, int index, T valeur);
}
