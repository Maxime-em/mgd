package org.mgd.jab.persistence;

import org.mgd.jab.dto.MondeDto;
import org.mgd.jab.objet.Monde;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class MondeJao extends Jao<MondeDto, Monde> {
    public MondeJao() {
        super(MondeDto.class, Monde.class);
    }

    @Override
    public MondeDto dto(Monde monde) {
        MondeDto mondeDto = new MondeDto();
        mondeDto.setPayss(new PaysJao().decharger(monde.getPayss()));
        return mondeDto;
    }

    @Override
    public void enrichir(MondeDto dto, Monde monde) throws JaoExecutionException, JaoParseException {
        monde.getPayss().addAll(new PaysJao().charger(dto.getPayss(), monde));
    }

    @Override
    protected void copier(Monde source, Monde cible) throws JaoExecutionException, JaoParseException {
        cible.getPayss().clear();
        cible.getPayss().addAll(new PaysJao().dupliquer(source.getPayss()));
    }
}
