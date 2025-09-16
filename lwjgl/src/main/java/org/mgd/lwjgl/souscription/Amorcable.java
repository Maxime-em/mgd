package org.mgd.lwjgl.souscription;

import org.mgd.lwjgl.Fenetre;

import java.util.Collection;

public interface Amorcable<A extends Identifiable> {
    default void avertirAmorcages(Fenetre fenetre, Collection<A> elements, boolean droite) {
        fenetre.gererAmorcages(elements.stream().map(Identifiable::uuid).toList(), droite);
    }
}
