package org.mgd.lwjgl.interne;

@FunctionalInterface
public interface DetecteurClavier {
    void invoquer(int cle, int code, int action, int modifications);
}
