package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Tangible;
import org.mgd.guerres.puniques.coeur.objet.Type;

@FunctionalInterface
public interface ChangementSelection {
    <T extends Type> void traiter(Tangible<T> objet);
}
