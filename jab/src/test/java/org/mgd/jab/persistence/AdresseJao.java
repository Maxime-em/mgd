package org.mgd.jab.persistence;

import org.mgd.jab.dto.AdresseDto;
import org.mgd.jab.objet.Adresse;
import org.mgd.jab.objet.Communaute;
import org.mgd.jab.objet.Monde;
import org.mgd.jab.objet.Personne;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

public class AdresseJao extends Jao<AdresseDto, Adresse> {
    public AdresseJao() {
        super(AdresseDto.class, Adresse.class);
    }

    @Override
    public AdresseDto dto(Adresse adresse) {
        AdresseDto adresseDto = new AdresseDto();
        adresseDto.setProprietaires(new PersonneJao().dechargerVersReferences(adresse.getProprietaires(), Personne.class, PersonneJao.class));
        adresseDto.setVoie(new VoieJao().decharger(adresse.getVoie()));
        adresseDto.setCommune(new CommuneJao().dechargerVersReference(adresse.getCommune(), Communaute.class, CommunauteJao.class));
        adresseDto.setPays(new PaysJao().dechargerVersReference(adresse.getPays(), Monde.class, MondeJao.class));
        return adresseDto;
    }

    @Override
    public void enrichir(AdresseDto dto, Adresse adresse) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getProprietaires(), "La liste des propriétaires dans une adresse devrait être une liste éventuellement vide");
        Verifications.nonNull(dto.getVoie(), "La voie dans une adresse devrait être non null");

        adresse.getProprietaires().addAll(new PersonneJao().chargerParReferences(dto.getProprietaires()));
        adresse.setVoie(new VoieJao().charger(dto.getVoie(), adresse));

        if (dto.getCommune() != null) {
            adresse.setCommune(new CommuneJao().chargerParReference(dto.getCommune()));
        }

        adresse.setPays(new PaysJao().chargerParReference(dto.getPays()));
    }

    @Override
    protected void copier(Adresse source, Adresse cible) throws JaoExecutionException, JaoParseException {
        source.getProprietaires().clear();
        source.getProprietaires().addAll(new PersonneJao().dupliquer(cible.getProprietaires()));
        cible.setVoie(source.getVoie());
        cible.setCommune(new CommuneJao().dupliquer(cible.getCommune()));
        cible.setPays(new PaysJao().dupliquer(cible.getPays()));
    }
}
