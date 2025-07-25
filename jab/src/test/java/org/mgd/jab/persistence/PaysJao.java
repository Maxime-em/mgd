package org.mgd.jab.persistence;

import org.mgd.jab.dto.PaysDto;
import org.mgd.jab.objet.Pays;

public class PaysJao extends Jao<PaysDto, Pays> {
    public PaysJao() {
        super(PaysDto.class, Pays.class);
    }

    @Override
    public PaysDto dto(Pays pays) {
        PaysDto paysDto = new PaysDto();
        paysDto.setNom(pays.getNom());
        return paysDto;
    }

    @Override
    public void enrichir(PaysDto dto, Pays pays) {
        pays.setNom(dto.getNom());
    }

    @Override
    protected void copier(Pays source, Pays cible) {
        cible.setNom(source.getNom());
    }
}
