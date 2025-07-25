package org.mgd.jab.persistence;

import org.mgd.jab.dto.CommunauteDto;
import org.mgd.jab.objet.Communaute;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class CommunauteJao extends Jao<CommunauteDto, Communaute> {
    public CommunauteJao() {
        super(CommunauteDto.class, Communaute.class);
    }

    @Override
    public CommunauteDto dto(Communaute objet) {
        CommunauteDto communauteDto = new CommunauteDto();
        communauteDto.setCommunes(new CommuneJao().decharger(objet.getCommunes()));
        return communauteDto;
    }

    @Override
    public void enrichir(CommunauteDto dto, Communaute communaute) throws JaoExecutionException, JaoParseException {
        communaute.getCommunes().addAll(new CommuneJao().charger(dto.getCommunes(), communaute));
    }

    @Override
    protected void copier(Communaute source, Communaute cible) throws JaoExecutionException, JaoParseException {
        cible.getCommunes().clear();
        cible.getCommunes().addAll(new CommuneJao().dupliquer(source.getCommunes()));
    }
}
