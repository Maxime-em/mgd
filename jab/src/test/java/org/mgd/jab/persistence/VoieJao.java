package org.mgd.jab.persistence;

import org.mgd.jab.dto.VoieDto;
import org.mgd.jab.objet.Voie;

public class VoieJao extends Jao<VoieDto, Voie> {
    public VoieJao() {
        super(VoieDto.class, Voie.class);
    }

    @Override
    public VoieDto dto(Voie voie) {
        VoieDto voieDto = new VoieDto();
        voieDto.setNumero(voie.getNumero());
        voieDto.setLibelle(voie.getLibelle());
        return voieDto;
    }

    @Override
    public void enrichir(VoieDto dto, Voie voie) {
        voie.setNumero(dto.getNumero());
        voie.setLibelle(dto.getLibelle());
    }

    @Override
    protected void copier(Voie source, Voie cible) {
        cible.setNumero(source.getNumero());
        cible.setLibelle(source.getLibelle());
    }
}
