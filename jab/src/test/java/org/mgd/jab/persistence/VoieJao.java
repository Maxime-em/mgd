package org.mgd.jab.persistence;

import org.mgd.jab.dto.VoieDto;
import org.mgd.jab.objet.Voie;

public class VoieJao extends Jao<VoieDto, Voie> {
    public VoieJao() {
        super(VoieDto.class, Voie.class);
    }

    @Override
    protected VoieDto to(Voie voie) {
        VoieDto voieDto = new VoieDto();
        voieDto.setNumero(voie.getNumero());
        voieDto.setLibelle(voie.getLibelle());
        return voieDto;
    }

    @Override
    protected void copier(Voie source, Voie cible) {
        cible.setNumero(source.getNumero());
        cible.setLibelle(source.getLibelle());
    }
}
