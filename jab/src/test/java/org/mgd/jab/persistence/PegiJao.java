package org.mgd.jab.persistence;

import org.mgd.jab.dto.PegiDto;
import org.mgd.jab.objet.Pegi;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

public class PegiJao extends Jao<PegiDto, Pegi> {
    public PegiJao() {
        super(PegiDto.class, Pegi.class);
    }

    @Override
    public PegiDto dto(Pegi pegi) {
        PegiDto dto = new PegiDto();
        dto.setAge(pegi.getAge());
        return dto;
    }

    @Override
    public void enrichir(PegiDto dto, Pegi pegi) throws VerificationException {
        Verifications.nonBorne(dto.getAge(), 0, 99, "L''age du système PEGI doit être compris entre {1} et {2} ans.");

        pegi.setAge(dto.getAge());
    }

    @Override
    protected void copier(Pegi source, Pegi cible) {
        cible.setAge(source.getAge());
    }
}
