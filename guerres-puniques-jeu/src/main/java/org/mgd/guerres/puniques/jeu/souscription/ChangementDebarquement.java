package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Armee;
import org.mgd.guerres.puniques.coeur.objet.Region;
import org.mgd.guerres.puniques.coeur.objet.Transport;

@FunctionalInterface
public interface ChangementDebarquement {
    void traiter(Armee armee, Transport transport, Region region);
}
