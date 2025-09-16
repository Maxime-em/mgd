package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Civilisation;
import org.mgd.guerres.puniques.coeur.objet.Region;
import org.mgd.guerres.puniques.coeur.objet.Tangible;
import org.mgd.guerres.puniques.coeur.objet.Type;

@FunctionalInterface
public interface ChangementDeploiement {
    <T extends Type> void traiter(Civilisation civilisation, Tangible<T> objet, Region region);
}
