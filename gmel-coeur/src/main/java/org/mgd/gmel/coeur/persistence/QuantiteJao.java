package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.dto.QuantiteDto;
import org.mgd.gmel.coeur.objet.Quantite;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.Arrays;
import java.util.stream.Collectors;

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
    public QuantiteDto dto(Quantite quantite) {
        QuantiteDto quantiteDto = new QuantiteDto();
        quantiteDto.setValeur(quantite.getValeur());
        quantiteDto.setMesure(quantite.getMesure());

        return quantiteDto;
    }

    @Override
    public void enrichir(QuantiteDto dto, Quantite quantite) throws VerificationException {
        Verifications.nonNull(dto.getMesure(), "La mesure d''une quantité devrait être une des valeurs {0}", Arrays.stream(Mesure.values()).map(Enum::name).collect(Collectors.joining(", ")));
        Verifications.nonStrictementNegatif(dto.getValeur(), "La valeur de la quantité doit être un entier positif");

        quantite.setValeur(dto.getValeur());
        quantite.setMesure(dto.getMesure());
    }

    @Override
    protected void copier(Quantite source, Quantite cible) {
        cible.setValeur(source.getValeur());
        cible.setMesure(source.getMesure());
    }
}
