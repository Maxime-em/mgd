package org.mgd.jab.persistence;

import org.mgd.jab.dto.AdresseDto;
import org.mgd.jab.objet.Adresse;

public class AdresseJao extends Jao<AdresseDto, Adresse> {
    public AdresseJao() {
        super(AdresseDto.class, Adresse.class);
    }

    @Override
    protected AdresseDto to(Adresse adresse) {
        AdresseDto adresseDto = new AdresseDto();
        adresseDto.setVoie(adresse.getVoie().dto());
        return adresseDto;
    }

    @Override
    protected void copier(Adresse source, Adresse cible) {
        cible.setVoie(source.getVoie());
    }
}
