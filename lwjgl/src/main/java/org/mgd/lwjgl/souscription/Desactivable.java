package org.mgd.lwjgl.souscription;

import java.util.Collection;

public interface Desactivable {
    Collection<Desactivation> desactivations();

    default void souscrire(Desactivation desactivation) {
        desactivations().add(desactivation);
    }

    default void avertirDesactivations() {
        desactivations().forEach(Desactivation::traiter);
    }
}
