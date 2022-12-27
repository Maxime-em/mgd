package org.mgd.jab.objet;

import org.mgd.jab.dto.AdresseDto;
import org.mgd.jab.persistence.AdresseJao;
import org.mgd.jab.persistence.VoieJao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.List;
import java.util.Objects;

public class Adresse extends Jo<AdresseDto> {
    private final JocArrayList<Personne> proprietaires = new JocArrayList<>(this);
    private final Joc<Voie> voie = new Joc<>(this);

    public List<Personne> getProprietaires() {
        return this.proprietaires;
    }

    public Voie getVoie() {
        return this.voie.get();
    }

    public void setVoie(Voie voie) {
        this.voie.set(voie);
    }

    @Override
    public AdresseDto dto() {
        return new AdresseJao().decharger(this);
    }

    @Override
    public void depuis(AdresseDto dto) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getProprietaires(), "La liste des propriétaires dans une adresse devrait être une liste éventuellement vide");
        Verifications.nonNull(dto.getVoie(), "La voie dans une adresse devrait être non null");

        setVoie(new VoieJao().charger(dto.getVoie(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Adresse adresse)) return false;
        return Objects.equals(voie, adresse.voie);
    }
}
