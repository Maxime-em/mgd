package org.mgd.lwjgl.element;

@FunctionalInterface
public interface SelectionCase {
    void traiter(int ligne, int colonne);
}
