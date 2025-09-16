package org.mgd.lwjgl.souscription;

@FunctionalInterface
public interface DetecteurAmorcage<T extends Identifiable> {
    void invoquer(T objet);
}
