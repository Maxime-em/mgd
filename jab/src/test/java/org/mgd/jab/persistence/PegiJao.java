package org.mgd.jab.persistence;

import org.mgd.jab.dto.PegiDto;
import org.mgd.jab.objet.Pegi;

public class PegiJao extends Jao<PegiDto, Pegi> {
    public PegiJao() {
        super(PegiDto.class, Pegi.class);
    }

    @Override
    protected PegiDto to(Pegi pegi) {
        PegiDto dto = new PegiDto();
        dto.setAge(pegi.getAge());
        return dto;
    }

    @Override
    protected void copier(Pegi source, Pegi cible) {
        cible.setAge(source.getAge());
    }
}
