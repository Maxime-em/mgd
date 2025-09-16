package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.DesDto;
import org.mgd.guerres.puniques.coeur.objet.Des;
import org.mgd.jab.persistence.Jao;

public class DesJao extends Jao<DesDto, Des> {
    public DesJao() {
        super(DesDto.class, Des.class);
    }

    @Override
    public DesDto dto(Des des) {
        DesDto desDto = new DesDto();
        desDto.setValeurs(des.getValeurs().stream().toList());
        desDto.setMaximum(des.getMaximum());
        desDto.setValeur(des.getValeur());
        return desDto;
    }

    @Override
    public void enrichir(DesDto dto, Des des) {
        des.getValeurs().addAll(dto.getValeurs());
        des.setMaximum(dto.getMaximum());
        des.setValeur(dto.getValeur());
    }

    @Override
    protected void copier(Des source, Des cible) {
        cible.getValeurs().clear();
        cible.getValeurs().addAll(source.getValeurs());
        cible.setMaximum(source.getMaximum());
        cible.setValeur(source.getValeur());
    }
}
