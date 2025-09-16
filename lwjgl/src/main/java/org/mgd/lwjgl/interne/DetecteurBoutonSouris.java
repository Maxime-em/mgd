package org.mgd.lwjgl.interne;

@FunctionalInterface
public interface DetecteurBoutonSouris {
    void invoquer(int bouton, int action, int mode);
}
