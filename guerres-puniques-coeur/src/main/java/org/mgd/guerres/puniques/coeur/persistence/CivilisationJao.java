package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.CivilisationDto;
import org.mgd.guerres.puniques.coeur.objet.Civilisation;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class CivilisationJao extends Jao<CivilisationDto, Civilisation> {
    public CivilisationJao() {
        super(CivilisationDto.class, Civilisation.class);
    }

    @Override
    public CivilisationDto dto(Civilisation civilisation) {
        CivilisationDto civilisationDto = new CivilisationDto();
        civilisationDto.setArmees(new ArmeeJao().decharger(civilisation.getArmees()));
        civilisationDto.setNombresArmeesMaximales(civilisation.getNombresArmeesMaximales());
        civilisationDto.setNom(civilisation.getNom());
        civilisationDto.setReserve(new ReserveJao().decharger(civilisation.getReserve()));

        return civilisationDto;
    }

    @Override
    public void enrichir(CivilisationDto dto, Civilisation civilisation) throws JaoExecutionException, JaoParseException {
        civilisation.getArmees().addAll(new ArmeeJao().charger(dto.getArmees(), civilisation));
        civilisation.getNombresArmeesMaximales().putAll(dto.getNombresArmeesMaximales());
        civilisation.setNom(dto.getNom());
        civilisation.setReserve(new ReserveJao().charger(dto.getReserve(), civilisation));
    }

    @Override
    protected void copier(Civilisation source, Civilisation cible) throws JaoExecutionException, JaoParseException {
        cible.getArmees().clear();
        cible.getArmees().addAll(new ArmeeJao().dupliquer(source.getArmees()));
        cible.getNombresArmeesMaximales().clear();
        cible.getNombresArmeesMaximales().putAll(source.getNombresArmeesMaximales());
        cible.setNom(source.getNom());
        cible.setReserve(new ReserveJao().dupliquer(source.getReserve()));
    }
}
