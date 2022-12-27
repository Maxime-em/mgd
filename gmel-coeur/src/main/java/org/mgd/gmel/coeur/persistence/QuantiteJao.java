package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.QuantiteDto;
import org.mgd.gmel.coeur.objet.Quantite;
import org.mgd.jab.persistence.Jao;

/**
 * Classe permettant de manipuler les objets métiers de type {@link Quantite} et de les charger depuis un système de
 * fichiers JSON via la classe de transfert {@link QuantiteDto}.
 *
 * @author Maxime
 */
public class QuantiteJao extends Jao<QuantiteDto, Quantite> {
    public QuantiteJao() {
        super(QuantiteDto.class, Quantite.class);
    }

    @Override
    protected QuantiteDto to(Quantite quantite) {
        QuantiteDto quantiteDto = new QuantiteDto();
        quantiteDto.setValeur(quantite.getValeur());
        quantiteDto.setMesure(quantite.getMesure());

        return quantiteDto;
    }

    @Override
    protected void copier(Quantite source, Quantite cible) {
        cible.setValeur(source.getValeur());
        cible.setMesure(source.getMesure());
    }
}
