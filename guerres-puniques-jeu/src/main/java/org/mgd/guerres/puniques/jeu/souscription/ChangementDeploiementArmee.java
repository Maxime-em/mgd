package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Armee;
import org.mgd.guerres.puniques.coeur.objet.Region;

@FunctionalInterface
public interface ChangementDeploiementArmee {
    void traiter(Armee armee, Region region);
}
