package org.mgd.commun;

@FunctionalInterface
public interface MatriceInsertion<T> {
    T obtenir(int ligne, int colonne, int index);
}
