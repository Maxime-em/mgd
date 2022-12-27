package org.mgd.jab.objet;

import org.mgd.jab.dto.PersonneDto;
import org.mgd.jab.persistence.JeuJao;
import org.mgd.jab.persistence.LivreJao;
import org.mgd.jab.persistence.PersonneJao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.List;
import java.util.Map;

public class Personne extends Jo<PersonneDto> {
    private final List<Jeu> jeux = new JocArrayList<>(this);
    private final Map<Integer, Livre> livres = new JocHashMap<>(this);
    private final Joc<Long> score = new Joc<>(this);

    public List<Jeu> getJeux() {
        return jeux;
    }

    public Map<Integer, Livre> getLivres() {
        return livres;
    }

    public Long getScore() {
        return score.get();
    }

    public void setScore(Long score) {
        this.score.set(score);
    }

    @Override
    public PersonneDto dto() {
        return new PersonneJao().decharger(this);
    }

    @Override
    public void depuis(PersonneDto dto) throws JaoParseException, JaoExecutionException, VerificationException {
        Verifications.nonNull(dto.getJeux(), "La liste des jeux d''une personne devrait être une liste éventuellement vide");
        Verifications.nonNull(dto.getLivres(), "La liste des livre d''une personne devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getJeux(),
                (jeuDto1, jeuDto2) -> (jeuDto1.getNom() == null && jeuDto2.getNom() == null) || (jeuDto1.getNom() != null && jeuDto1.getNom().equals(jeuDto2.getNom())),
                "La liste des jeux ne doit pas contenir plusieurs jeux avec le même nom");
        Verifications.nonNegatif(dto.getScore(), "Le score d''une personne doit être strictement positif");

        getJeux().addAll(new JeuJao().charger(dto.getJeux(), this));
        getLivres().putAll(new LivreJao().charger(dto.getLivres(), this));
        setScore(dto.getScore());
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Personne personne)) return false;
        return ((JocArrayList<Jeu>) jeux).idem(personne.jeux) && ((JocHashMap<Integer, Livre>) livres).idem(personne.livres) && score.idem(personne.score);
    }
}
