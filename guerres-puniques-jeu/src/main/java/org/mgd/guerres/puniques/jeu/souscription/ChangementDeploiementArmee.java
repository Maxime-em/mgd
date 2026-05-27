package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Armee;
import org.mgd.guerres.puniques.coeur.objet.Civilisation;
import org.mgd.guerres.puniques.coeur.objet.Region;

@FunctionalInterface
public interface ChangementDeploiementArmee {
    void traiter(Civilisation civilisation, Armee armee, Region region);
}
