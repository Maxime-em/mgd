package org.mgd.lwjgl.souscription;

@FunctionalInterface
public interface DetecteurBoutonSouris {
    void invoquer(int bouton, int action, int mode);
}
