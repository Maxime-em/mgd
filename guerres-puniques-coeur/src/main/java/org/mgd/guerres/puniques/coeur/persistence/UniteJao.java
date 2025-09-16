package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.UniteDto;
import org.mgd.guerres.puniques.coeur.objet.Unite;
import org.mgd.jab.persistence.Jao;

public class UniteJao extends Jao<UniteDto, Unite> {
    public UniteJao() {
        super(UniteDto.class, Unite.class);
    }

    @Override
    public UniteDto dto(Unite unite) {
        UniteDto uniteDto = new UniteDto();
        uniteDto.setType(unite.getType());
        uniteDto.setVie(unite.getVie());

        return uniteDto;
    }

    @Override
    public void enrichir(UniteDto dto, Unite unite) {
        unite.setType(dto.getType());
        unite.setVie(dto.getVie());
    }

    @Override
    protected void copier(Unite source, Unite cible) {
        cible.setType(source.getType());
        cible.setVie(source.getVie());
    }
}
