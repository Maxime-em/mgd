package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.RegionDto;
import org.mgd.guerres.puniques.coeur.objet.Region;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class RegionJao extends Jao<RegionDto, Region> {
    public RegionJao() {
        super(RegionDto.class, Region.class);
    }

    @Override
    public RegionDto dto(Region region) {
        RegionDto regionDto = new RegionDto();
        regionDto.setAlignements(new AlignementJao().decharger(region.getAlignements()));
        regionDto.setTypes(new TypeRegionJao().decharger(region.getTypes()));
        regionDto.setArmee(new ArmeeJao().decharger(region.getArmees()));
        regionDto.setTransports(new TransportJao().decharger(region.getTransports()));

        return regionDto;
    }

    @Override
    public void enrichir(RegionDto dto, Region region) throws JaoExecutionException, JaoParseException {
        region.getAlignements().addAll(new AlignementJao().charger(dto.getAlignements(), region));
        region.getTypes().addAll(new TypeRegionJao().charger(dto.getTypes(), region));
        region.getArmees().addAll(new ArmeeJao().charger(dto.getArmee(), region));
        region.getTransports().addAll(new TransportJao().charger(dto.getTransports(), region));
    }

    @Override
    protected void copier(Region source, Region cible) throws JaoExecutionException, JaoParseException {
        cible.getAlignements().clear();
        cible.getAlignements().addAll(new AlignementJao().dupliquer(source.getAlignements()));
        cible.getTypes().clear();
        cible.getTypes().addAll(new TypeRegionJao().dupliquer(source.getTypes()));
        cible.getArmees().clear();
        cible.getArmees().addAll(new ArmeeJao().dupliquer(source.getArmees()));
        cible.getTransports().clear();
        cible.getTransports().addAll(new TransportJao().dupliquer(source.getTransports()));
    }
}
