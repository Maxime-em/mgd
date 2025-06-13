package org.mgd.jab.persistence;

import org.mgd.jab.dto.PaysDto;
import org.mgd.jab.objet.Pays;

public class PaysJao extends Jao<PaysDto, Pays> {
    public PaysJao() {
        super(PaysDto.class, Pays.class);
    }

    @Override
    protected PaysDto to(Pays pays) {
        PaysDto paysDto = new PaysDto();
        paysDto.setNom(pays.getNom());
        return paysDto;
    }

    @Override
    protected void copier(Pays source, Pays cible) {
        cible.setNom(source.getNom());
    }
}
