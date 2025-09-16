package org.mgd.lwjgl.souscription;

@FunctionalInterface
public interface DetecteurClavier {
    void invoquer(int cle, int code, int action, int modifications);
}
