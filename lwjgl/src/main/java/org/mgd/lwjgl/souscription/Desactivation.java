package org.mgd.lwjgl.souscription;

@FunctionalInterface
public interface Desactivation<T> {
    void traiter(T objet);
}
