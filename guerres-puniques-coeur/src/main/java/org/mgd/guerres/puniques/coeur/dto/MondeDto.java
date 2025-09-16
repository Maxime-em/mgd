package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.List;

public class MondeDto extends Dto {
    private List<TypeRegionDto> types;
    private RegionDto[][] regions;

    public List<TypeRegionDto> getTypes() {
        return types;
    }

    public void setTypes(List<TypeRegionDto> types) {
        this.types = types;
    }

    public RegionDto[][] getRegions() {
        return regions;
    }

    public void setRegions(RegionDto[][] regions) {
        this.regions = regions;
    }
}
