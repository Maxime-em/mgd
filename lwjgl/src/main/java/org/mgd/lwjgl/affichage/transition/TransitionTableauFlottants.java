package org.mgd.lwjgl.affichage.transition;

import java.util.Arrays;

public abstract class TransitionTableauFlottants<O> extends Transition<O, Float[]> {
    protected TransitionTableauFlottants(O objet, Float[] depart, Float[] arrive, long duree) {
        super(objet, depart, arrive, duree);
    }

    @Override
    protected Float[] multiplierParScalaire(Double scalaire, Float[] valeur) {
        return Arrays.stream(valeur).map(flottant -> (float) (scalaire * flottant)).toArray(Float[]::new);
    }

    @Override
    protected Float[] sommer(Float[] valeur1, Float[] valeur2) {
        Float[] resultat = new Float[valeur1.length];
        for (int rang = 0; rang < valeur1.length; rang++) {
            resultat[rang] = valeur1[rang] + valeur2[rang];
        }
        return resultat;
    }
}
