package org.mgd.guerres.puniques.jeu.souscription;

import org.mgd.guerres.puniques.coeur.objet.Armee;
import org.mgd.guerres.puniques.coeur.objet.Civilisation;

@FunctionalInterface
public interface ChangementAttaqueCivilisation {
    void traiter(Armee armee, Civilisation civilisation);
}
