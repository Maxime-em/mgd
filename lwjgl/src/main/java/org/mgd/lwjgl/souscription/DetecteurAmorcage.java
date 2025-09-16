package org.mgd.lwjgl.souscription;

import org.mgd.lwjgl.commun.Identifiable;

@FunctionalInterface
public interface DetecteurAmorcage<T extends Identifiable> {
    void invoquer(T objet);
}
