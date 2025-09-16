package org.mgd.lwjgl.interne;

@FunctionalInterface
public interface FluxPositions2D<T> {
    void traiter(float haut, float droite, float bas, float gauche, T objet);
}
