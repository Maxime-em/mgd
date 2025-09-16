package org.mgd.lwjgl.souscription;

@FunctionalInterface
public interface Activation<P, T> {
    void traiter(P parent, T objet);
}
