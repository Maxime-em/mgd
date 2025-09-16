package org.mgd.lwjgl.souscription;

@FunctionalInterface
public interface Activation<T> {
    void traiter(T objet);
}
