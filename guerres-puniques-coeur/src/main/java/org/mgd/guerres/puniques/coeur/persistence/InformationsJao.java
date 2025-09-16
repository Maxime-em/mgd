package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.InformationsDto;
import org.mgd.guerres.puniques.coeur.objet.Informations;
import org.mgd.jab.persistence.Jao;

public class InformationsJao extends Jao<InformationsDto, Informations> {
    public InformationsJao() {
        super(InformationsDto.class, Informations.class);
    }

    @Override
    public InformationsDto dto(Informations informations) {
        InformationsDto informationsDto = new InformationsDto();
        informationsDto.setNom(informations.getNom());
        return informationsDto;
    }

    @Override
    public void enrichir(InformationsDto dto, Informations informations) {
        informations.setNom(dto.getNom());
    }

    @Override
    protected void copier(Informations source, Informations cible) {
        cible.setNom(source.getNom());
    }
}
