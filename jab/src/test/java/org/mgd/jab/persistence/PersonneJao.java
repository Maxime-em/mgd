package org.mgd.jab.persistence;

import org.mgd.jab.dto.PersonneDto;
import org.mgd.jab.objet.Personne;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

public class PersonneJao extends Jao<PersonneDto, Personne> {
    public PersonneJao() {
        super(PersonneDto.class, Personne.class);
    }

    @Override
    public PersonneDto dto(Personne personne) {
        PersonneDto dto = new PersonneDto();
        dto.setJeux(new JeuJao().decharger(personne.getJeux()));
        dto.setLivres(new LivreJao().decharger(personne.getLivres()));
        dto.setScore(personne.getScore());
        return dto;
    }

    @Override
    public void enrichir(PersonneDto dto, Personne personne) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getJeux(), "La liste des jeux d''une personne devrait être une liste éventuellement vide");
        Verifications.nonNull(dto.getLivres(), "La liste des livre d''une personne devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getJeux(),
                (jeuDto1, jeuDto2) -> (jeuDto1.getNom() == null && jeuDto2.getNom() == null) || (jeuDto1.getNom() != null && jeuDto1.getNom().equals(jeuDto2.getNom())),
                "La liste des jeux ne doit pas contenir plusieurs jeux avec le même nom");
        Verifications.nonNegatif(dto.getScore(), "Le score d''une personne doit être strictement positif");

        personne.getJeux().addAll(new JeuJao().charger(dto.getJeux(), personne));
        personne.getLivres().putAll(new LivreJao().charger(dto.getLivres(), personne));
        personne.setScore(dto.getScore());
    }

    @Override
    protected void copier(Personne source, Personne cible) throws JaoExecutionException, JaoParseException {
        cible.getJeux().clear();
        cible.getJeux().addAll(new JeuJao().dupliquer(source.getJeux()));
        cible.getLivres().clear();
        cible.getLivres().putAll(new LivreJao().dupliquer(source.getLivres()));
        cible.setScore(source.getScore());
    }
}
