package org.mgd.jab.persistence;

import org.mgd.jab.dto.LivreDto;
import org.mgd.jab.objet.Livre;
import org.mgd.jab.persistence.exception.JaoExecutionException;

public class LivreJao extends Jao<LivreDto, Livre> {
    public LivreJao() {
        super(LivreDto.class, Livre.class);
    }

    @Override
    protected LivreDto to(Livre livre) {
        LivreDto dto = new LivreDto();
        dto.setNom(livre.getNom());
        dto.setChapitres(new ChapitreJao().decharger(new PegiJao(), livre.getChapitres()));
        return dto;
    }

    @Override
    protected void copier(Livre source, Livre cible) throws JaoExecutionException {
        cible.getChapitres().clear();
        cible.getChapitres().putAll(new ChapitreJao().dupliquer(new PegiJao(), source.getChapitres()));
        cible.setNom(source.getNom());
    }
}
