package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.PeriodeDto;
import org.mgd.gmel.coeur.objet.Periode;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;
import org.mgd.temps.LocalRepas;

public class PeriodeJao extends Jao<PeriodeDto, Periode> {
    public PeriodeJao() {
        super(PeriodeDto.class, Periode.class);
    }

    @Override
    public PeriodeDto dto(Periode periode) {
        PeriodeDto periodeDto = new PeriodeDto();
        periodeDto.setRepas(periode.getRepas());
        periodeDto.setTaille(periode.getTaille());

        return periodeDto;
    }

    @Override
    public void enrichir(PeriodeDto dto, Periode periode) throws VerificationException {
        Verifications.nonNull(dto.getRepas(), "Une période doit contenir le repas");
        Verifications.nonNegatif(dto.getTaille(), "La taille d''une période doit être un entier strictement positif");

        periode.setRepas(dto.getRepas());
        periode.setTaille(dto.getTaille());
    }

    @Override
    protected void copier(Periode source, Periode cible) {
        cible.setRepas(LocalRepas.depuis(source.getRepas()));
        cible.setTaille(source.getTaille());
    }
}
