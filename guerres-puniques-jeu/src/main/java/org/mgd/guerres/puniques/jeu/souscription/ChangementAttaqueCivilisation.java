package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Civilisation;
import org.mgd.guerres.puniques.coeur.objet.Tangible;
import org.mgd.guerres.puniques.coeur.objet.Type;

@FunctionalInterface
public interface ChangementAttaqueCivilisation {
    <T extends Type> void traiter(Tangible<T> attaquant, Civilisation civilisation);
}
