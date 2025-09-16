package org.mgd.lwjgl.souscription;

@FunctionalInterface
public interface DetecteurErreur {
    void invoquer(long cle, long description);
}
