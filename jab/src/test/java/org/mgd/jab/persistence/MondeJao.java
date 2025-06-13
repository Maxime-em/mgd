package org.mgd.jab.persistence;

import org.mgd.jab.dto.MondeDto;
import org.mgd.jab.objet.Monde;
import org.mgd.jab.persistence.exception.JaoExecutionException;

public class MondeJao extends Jao<MondeDto, Monde> {
    public MondeJao() {
        super(MondeDto.class, Monde.class);
    }

    @Override
    protected MondeDto to(Monde monde) {
        MondeDto mondeDto = new MondeDto();
        mondeDto.setPayss(new PaysJao().decharger(monde.getPayss()));
        return mondeDto;
    }

    @Override
    protected void copier(Monde source, Monde cible) throws JaoExecutionException {
        cible.getPayss().clear();
        cible.getPayss().addAll(new PaysJao().dupliquer(source.getPayss()));
    }
}
