package org.mgd.jab.persistence;

import org.mgd.jab.dto.CommuneDto;
import org.mgd.jab.objet.Commune;

public class CommuneJao extends Jao<CommuneDto, Commune> {
    public CommuneJao() {
        super(CommuneDto.class, Commune.class);
    }

    @Override
    public CommuneDto dto(Commune objet) {
        CommuneDto communeDto = new CommuneDto();
        communeDto.setNom(objet.getNom());
        communeDto.setCode(objet.getCode());
        return communeDto;
    }

    @Override
    public void enrichir(CommuneDto dto, Commune commune) {
        commune.setNom(dto.getNom());
        commune.setCode(dto.getCode());
    }

    @Override
    protected void copier(Commune source, Commune cible) {
        source.setNom(cible.getNom());
        source.setCode(cible.getCode());
    }
}
