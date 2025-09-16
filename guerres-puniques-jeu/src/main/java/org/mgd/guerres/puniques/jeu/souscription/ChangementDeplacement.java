package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Region;
import org.mgd.guerres.puniques.coeur.objet.Tangible;
import org.mgd.guerres.puniques.coeur.objet.Type;

@FunctionalInterface
public interface ChangementDeplacement {
    <T extends Type> void traiter(Tangible<T> objet, Region region);
}
