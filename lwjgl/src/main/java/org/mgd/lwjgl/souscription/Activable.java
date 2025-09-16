package org.mgd.lwjgl.souscription;

import java.util.Collection;

public interface Activable<A> {
    A elementActivation();

    Collection<Activation<A>> activations();

    default void souscrire(Activation<A> activation) {
        activations().add(activation);
    }

    default void avertirActivations() {
        activations().forEach(activation -> activation.traiter(elementActivation()));
    }
}
