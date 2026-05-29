package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Tangible;
import org.mgd.guerres.puniques.coeur.objet.Type;

@FunctionalInterface
public interface ChangementAttaque {
    <T extends Type, U extends Type> void traiter(Tangible<T> attaquant, Tangible<U> defenseur);
}
