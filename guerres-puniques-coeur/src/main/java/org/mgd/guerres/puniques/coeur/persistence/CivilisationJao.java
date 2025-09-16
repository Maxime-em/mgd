package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.CivilisationDto;
import org.mgd.guerres.puniques.coeur.objet.Civilisation;
import org.mgd.guerres.puniques.coeur.objet.Partie;
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
        civilisationDto.setTypesUnites(new TypeUniteJao().decharger(civilisation.getTypesUnites()));
        civilisationDto.setTypesTransports(new TypeTransportJao().decharger(civilisation.getTypesTransports()));
        civilisationDto.setTypeArmees(new TypeArmeeJao().decharger(civilisation.getTypeArmees()));
        civilisationDto.setTransports(new TransportJao().decharger(civilisation.getTransports()));
        civilisationDto.setArmees(new ArmeeJao().decharger(civilisation.getArmees()));
        civilisationDto.setNom(civilisation.getNom());
        civilisationDto.setReserve(new ReserveJao().decharger(civilisation.getReserve()));
        civilisationDto.setCapitale(new RegionJao().dechargerVersReference(civilisation.getCapitale(), Partie.class, PartieJao.class));

        return civilisationDto;
    }

    @Override
    public void enrichir(CivilisationDto dto, Civilisation civilisation) throws JaoExecutionException, JaoParseException {
        civilisation.getTypesUnites().addAll(new TypeUniteJao().charger(dto.getTypesUnites(), civilisation));
        civilisation.getTypesTransports().addAll(new TypeTransportJao().charger(dto.getTypesTransports(), civilisation));
        civilisation.getTypeArmees().addAll(new TypeArmeeJao().charger(dto.getTypeArmees(), civilisation));
        civilisation.getTransports().addAll(new TransportJao().charger(dto.getTransports(), civilisation));
        civilisation.getArmees().addAll(new ArmeeJao().charger(dto.getArmees(), civilisation));
        civilisation.setNom(dto.getNom());
        civilisation.setReserve(new ReserveJao().charger(dto.getReserve(), civilisation));

        postChargement(civilisation, objet -> objet.setCapitale(new RegionJao().chargerParReference(dto.getCapitale())));
    }

    @Override
    protected void copier(Civilisation source, Civilisation cible) throws JaoExecutionException, JaoParseException {
        cible.getTypesUnites().clear();
        cible.getTypesUnites().addAll(new TypeUniteJao().dupliquer(source.getTypesUnites()));
        cible.getTypesTransports().clear();
        cible.getTypesTransports().addAll(new TypeTransportJao().dupliquer(source.getTypesTransports()));
        cible.getTypeArmees().clear();
        cible.getTypeArmees().addAll(new TypeArmeeJao().dupliquer(source.getTypeArmees()));
        cible.getTransports().clear();
        cible.getTransports().addAll(new TransportJao().dupliquer(source.getTransports()));
        cible.getArmees().clear();
        cible.getArmees().addAll(new ArmeeJao().dupliquer(source.getArmees()));
        cible.setNom(source.getNom());
        cible.setReserve(new ReserveJao().dupliquer(source.getReserve()));
        cible.setCapitale(new RegionJao().dupliquer(source.getCapitale()));
    }
}
