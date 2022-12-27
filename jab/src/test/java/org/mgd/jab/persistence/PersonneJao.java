package org.mgd.jab.persistence;

import org.mgd.jab.dto.PersonneDto;
import org.mgd.jab.objet.Personne;
import org.mgd.jab.persistence.exception.JaoExecutionException;

public class PersonneJao extends Jao<PersonneDto, Personne> {
    public PersonneJao() {
        super(PersonneDto.class, Personne.class);
    }

    @Override
    protected PersonneDto to(Personne personne) {
        PersonneDto dto = new PersonneDto();
        dto.setJeux(new JeuJao().decharger(personne.getJeux()));
        dto.setLivres(new LivreJao().decharger(personne.getLivres()));
        dto.setScore(personne.getScore());
        return dto;
    }

    @Override
    protected void copier(Personne source, Personne cible) throws JaoExecutionException {
        cible.getJeux().clear();
        cible.getJeux().addAll(new JeuJao().dupliquer(source.getJeux()));
        cible.getLivres().clear();
        cible.getLivres().putAll(new LivreJao().dupliquer(source.getLivres()));
        cible.setScore(source.getScore());
    }
}
