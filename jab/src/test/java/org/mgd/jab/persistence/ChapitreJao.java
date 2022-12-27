package org.mgd.jab.persistence;

import org.mgd.jab.dto.ChapitreDto;
import org.mgd.jab.objet.Chapitre;

public class ChapitreJao extends Jao<ChapitreDto, Chapitre> {
    public ChapitreJao() {
        super(ChapitreDto.class, Chapitre.class);
    }

    @Override
    protected ChapitreDto to(Chapitre chapitre) {
        ChapitreDto dto = new ChapitreDto();
        dto.setNom(chapitre.getNom());
        return dto;
    }

    @Override
    protected void copier(Chapitre source, Chapitre cible) {
        cible.setNom(source.getNom());
    }
}
