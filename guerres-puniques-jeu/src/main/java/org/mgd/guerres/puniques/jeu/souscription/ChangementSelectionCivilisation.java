package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Civilisation;

@FunctionalInterface
public interface ChangementSelectionCivilisation {
    void traiter(Civilisation civilisation);
}
