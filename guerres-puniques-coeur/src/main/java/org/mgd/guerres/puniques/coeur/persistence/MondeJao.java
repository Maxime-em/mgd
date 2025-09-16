package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.MondeDto;
import org.mgd.guerres.puniques.coeur.objet.Monde;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class MondeJao extends Jao<MondeDto, Monde> {
    public MondeJao() {
        super(MondeDto.class, Monde.class);
    }

    @Override
    public MondeDto dto(Monde monde) {
        MondeDto mondeDto = new MondeDto();
        mondeDto.setRegions(new RegionJao().decharger(monde.getRegions()));
        return mondeDto;
    }

    @Override
    public void enrichir(MondeDto dto, Monde monde) throws JaoExecutionException, JaoParseException {
        monde.setRegions(new RegionJao().charger(dto.getRegions(), monde));
    }

    @Override
    protected void copier(Monde source, Monde cible) throws JaoExecutionException, JaoParseException {
        cible.setRegions(new RegionJao().dupliquer(source.getRegions()));
    }
}
