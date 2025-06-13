package org.mgd.jab.persistence;

import org.mgd.jab.dto.CommuneDto;
import org.mgd.jab.objet.Commune;

public class CommuneJao extends Jao<CommuneDto, Commune> {
    public CommuneJao() {
        super(CommuneDto.class, Commune.class);
    }

    @Override
    protected CommuneDto to(Commune objet) {
        CommuneDto communeDto = new CommuneDto();
        communeDto.setNom(objet.getNom());
        communeDto.setCode(objet.getCode());
        return communeDto;
    }

    @Override
    protected void copier(Commune source, Commune cible) {
        source.setNom(cible.getNom());
        source.setCode(cible.getCode());
    }
}
