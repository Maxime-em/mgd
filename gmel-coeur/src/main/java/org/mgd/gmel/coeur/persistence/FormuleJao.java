package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.FormuleDto;
import org.mgd.gmel.coeur.objet.Bibliotheque;
import org.mgd.gmel.coeur.objet.Formule;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class FormuleJao extends Jao<FormuleDto, Formule> {
    public FormuleJao() {
        super(FormuleDto.class, Formule.class);
    }

    @Override
    public FormuleDto dto(Formule formule) {
        FormuleDto formuleDto = new FormuleDto();
        formuleDto.setRecette(new RecetteJao().dechargerVersReference(formule.getRecette(), Bibliotheque.class, BibliothequeJao.class));
        formuleDto.setPeriode(new PeriodeJao().decharger(formule.getPeriode()));
        formuleDto.setNombreConvives(formule.getNombreConvives());

        return formuleDto;
    }

    @Override
    public void enrichir(FormuleDto dto, Formule formule) throws JaoExecutionException, JaoParseException {
        if (dto.getRecette() != null) {
            formule.setRecette(new RecetteJao().chargerParReference(dto.getRecette()));
        }

        if (dto.getPeriode() != null) {
            formule.setPeriode(new PeriodeJao().charger(dto.getPeriode(), formule));
        }

        formule.setNombreConvives(dto.getNombreConvives());
    }

    @Override
    public void copier(Formule source, Formule cible) throws JaoExecutionException, JaoParseException {
        cible.setRecette(new RecetteJao().dupliquer(source.getRecette()));
        cible.setPeriode(new PeriodeJao().dupliquer(source.getPeriode()));
        cible.setNombreConvives(source.getNombreConvives());
    }
}
