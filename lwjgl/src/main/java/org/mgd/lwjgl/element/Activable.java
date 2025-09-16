package org.mgd.lwjgl.element;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.souscription.Activation;
import org.mgd.lwjgl.souscription.Desactivation;

import java.util.Collection;

public interface Activable<P, A, D> extends Producteur {
    boolean survoler(Vision vision, Fenetre.EvenementSouris evenementSouris);

    void desurvoler();

    void activer();

    void deactiver();

    default void maj(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        if (evenementSouris.inacheve()) {
            if (survoler(vision, evenementSouris)) {
                if (evenementSouris.selection()) {
                    activer();
                }
                evenementSouris.comsommer();
            }
        } else {
            desurvoler();
            if (evenementSouris.selection()) {
                deactiver();
            }
        }
    }

    A elementActivation(P parent);

    D elementDesactivation();

    Collection<Activation<P, A>> activations();

    Collection<Desactivation<D>> desactivations();

    default void souscrire(Activation<P, A> activation) {
        activations().add(activation);
    }

    default void souscrire(Desactivation<D> desactivation) {
        desactivations().add(desactivation);
    }

    default void avertirActivations(P parent, Collection<Activation<P, A>> activations) {
        activations.forEach(activation -> activation.traiter(parent, elementActivation(parent)));
    }

    default void avertirDesactivations(Collection<Desactivation<D>> desactivations) {
        desactivations.forEach(desactivation -> desactivation.traiter(elementDesactivation()));
    }
}
