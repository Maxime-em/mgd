package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Armee;

@FunctionalInterface
public interface ChangementAttaqueArmee {
    void traiter(Armee attaquant, Armee defenseur);
}
