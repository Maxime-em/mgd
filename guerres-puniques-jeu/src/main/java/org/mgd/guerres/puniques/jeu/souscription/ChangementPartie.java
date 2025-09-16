package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Partie;

@FunctionalInterface
public interface ChangementPartie {
    void traiter(Partie partie);
}
