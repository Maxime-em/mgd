package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.ReserveDto;
import org.mgd.guerres.puniques.coeur.objet.Reserve;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class ReserveJao extends Jao<ReserveDto, Reserve> {
    public ReserveJao() {
        super(ReserveDto.class, Reserve.class);
    }

    @Override
    public ReserveDto dto(Reserve reserve) {
        ReserveDto reserveDto = new ReserveDto();
        reserveDto.setUnites(new UniteJao().decharger(reserve.getUnites()));
        reserveDto.setNombresUnitesMaximales(reserve.getNombresUnitesMaximales());

        return reserveDto;
    }

    @Override
    public void enrichir(ReserveDto dto, Reserve reserve) throws JaoExecutionException, JaoParseException {
        reserve.getUnites().addAll(new UniteJao().charger(dto.getUnites(), reserve));
        reserve.getNombresUnitesMaximales().putAll(dto.getNombresUnitesMaximales());
    }

    @Override
    protected void copier(Reserve source, Reserve cible) throws JaoExecutionException, JaoParseException {
        cible.getUnites().clear();
        cible.getUnites().addAll(new UniteJao().dupliquer(source.getUnites()));
        cible.getNombresUnitesMaximales().putAll(source.getNombresUnitesMaximales());
    }
}
