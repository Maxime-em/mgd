package org.mgd.jab.persistence;

import org.mgd.jab.dto.LivreDto;
import org.mgd.jab.objet.Livre;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

public class LivreJao extends Jao<LivreDto, Livre> {
    public LivreJao() {
        super(LivreDto.class, Livre.class);
    }

    @Override
    public LivreDto dto(Livre livre) {
        LivreDto dto = new LivreDto();
        dto.setNom(livre.getNom());
        dto.setChapitres(new ChapitreJao().decharger(new PegiJao(), livre.getChapitres()));
        return dto;
    }

    @Override
    public void enrichir(LivreDto dto, Livre livre) throws JaoParseException, VerificationException {
        Verifications.nonNull(dto.getChapitres(), "La liste des chapitres d''un livre devrait être une liste éventuellement vide");

        livre.setNom(dto.getNom());
        livre.getChapitres().putAll(new ChapitreJao().charger(new PegiJao(), dto.getChapitres(), livre));
    }

    @Override
    protected void copier(Livre source, Livre cible) throws JaoExecutionException, JaoParseException {
        cible.getChapitres().clear();
        cible.getChapitres().putAll(new ChapitreJao().dupliquer(new PegiJao(), source.getChapitres()));
        cible.setNom(source.getNom());
    }
}
