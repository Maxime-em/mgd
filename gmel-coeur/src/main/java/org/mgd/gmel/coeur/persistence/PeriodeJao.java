package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.PeriodeDto;
import org.mgd.gmel.coeur.objet.Periode;
import org.mgd.jab.persistence.Jao;
import org.mgd.temps.LocalRepas;

public class PeriodeJao extends Jao<PeriodeDto, Periode> {
    public PeriodeJao() {
        super(PeriodeDto.class, Periode.class);
    }

    @Override
    protected PeriodeDto to(Periode periode) {
        PeriodeDto periodeDto = new PeriodeDto();
        periodeDto.setRepas(periode.getRepas());
        periodeDto.setTaille(periode.getTaille());

        return periodeDto;
    }

    @Override
    protected void copier(Periode source, Periode cible) {
        cible.setRepas(LocalRepas.depuis(source.getRepas()));
        cible.setTaille(source.getTaille());
    }
}
