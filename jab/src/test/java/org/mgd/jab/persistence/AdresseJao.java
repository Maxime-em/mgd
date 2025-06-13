package org.mgd.jab.persistence;

import org.mgd.jab.dto.AdresseDto;
import org.mgd.jab.objet.Adresse;
import org.mgd.jab.objet.Communaute;
import org.mgd.jab.objet.Monde;
import org.mgd.jab.objet.Personne;
import org.mgd.jab.persistence.exception.JaoExecutionException;

public class AdresseJao extends Jao<AdresseDto, Adresse> {
    public AdresseJao() {
        super(AdresseDto.class, Adresse.class);
    }

    @Override
    protected AdresseDto to(Adresse adresse) {
        AdresseDto adresseDto = new AdresseDto();
        adresseDto.setProprietaires(new PersonneJao().dechargerVersReferences(adresse.getProprietaires(), Personne.class, PersonneJao.class));
        adresseDto.setVoie(new VoieJao().decharger(adresse.getVoie()));
        adresseDto.setCommune(new CommuneJao().dechargerVersReference(adresse.getCommune(), Communaute.class, CommunauteJao.class));
        adresseDto.setPays(new PaysJao().dechargerVersReference(adresse.getPays(), Monde.class, MondeJao.class));
        return adresseDto;
    }

    @Override
    protected void copier(Adresse source, Adresse cible) throws JaoExecutionException {
        source.getProprietaires().clear();
        source.getProprietaires().addAll(new PersonneJao().dupliquer(cible.getProprietaires()));
        cible.setVoie(source.getVoie());
        cible.setCommune(new CommuneJao().dupliquer(cible.getCommune()));
        cible.setPays(new PaysJao().dupliquer(cible.getPays()));
    }
}
