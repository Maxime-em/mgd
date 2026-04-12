package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.TypeRegionDto;
import org.mgd.guerres.puniques.coeur.objet.TypeRegion;
import org.mgd.jab.persistence.Jao;

public class TypeRegionJao extends Jao<TypeRegionDto, TypeRegion> {
    public TypeRegionJao() {
        super(TypeRegionDto.class, TypeRegion.class);
    }

    @Override
    public TypeRegionDto dto(TypeRegion type) {
        TypeRegionDto typeRegionDto = new TypeRegionDto();
        typeRegionDto.setCode(type.getCode());

        return typeRegionDto;
    }

    @Override
    public void enrichir(TypeRegionDto dto, TypeRegion type) {
        type.setCode(dto.getCode());
    }

    @Override
    protected void copier(TypeRegion source, TypeRegion cible) {
        cible.setCode(source.getCode());
    }
}
