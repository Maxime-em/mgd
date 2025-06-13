package org.mgd.jab.persistence;

import org.mgd.jab.dto.CommunauteDto;
import org.mgd.jab.objet.Communaute;
import org.mgd.jab.persistence.exception.JaoExecutionException;

public class CommunauteJao extends Jao<CommunauteDto, Communaute> {
    public CommunauteJao() {
        super(CommunauteDto.class, Communaute.class);
    }

    @Override
    protected CommunauteDto to(Communaute objet) {
        CommunauteDto communauteDto = new CommunauteDto();
        communauteDto.setCommunes(new CommuneJao().decharger(objet.getCommunes()));
        return communauteDto;
    }

    @Override
    protected void copier(Communaute source, Communaute cible) throws JaoExecutionException {
        cible.getCommunes().clear();
        cible.getCommunes().addAll(new CommuneJao().dupliquer(source.getCommunes()));
    }
}
