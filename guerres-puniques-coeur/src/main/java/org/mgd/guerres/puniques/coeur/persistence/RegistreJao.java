package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.RegistreDto;
import org.mgd.guerres.puniques.coeur.objet.Registre;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class RegistreJao extends Jao<RegistreDto, Registre> {
    public RegistreJao() {
        super(RegistreDto.class, Registre.class);
    }

    @Override
    public RegistreDto dto(Registre registre) {
        RegistreDto registreDto = new RegistreDto();
        registreDto.setInformations(new InformationsJao().decharger(registre.getInformations()));
        return registreDto;
    }

    @Override
    public void enrichir(RegistreDto dto, Registre registre) throws JaoParseException {
        registre.getInformations().putAll(new InformationsJao().charger(dto.getInformations(), registre));
    }

    @Override
    protected void copier(Registre source, Registre cible) throws JaoExecutionException, JaoParseException {
        cible.getInformations().clear();
        cible.getInformations().putAll(new InformationsJao().dupliquer(source.getInformations()));
    }
}
