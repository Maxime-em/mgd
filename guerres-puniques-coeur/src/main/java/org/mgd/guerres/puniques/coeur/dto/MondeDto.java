package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

public class MondeDto extends Dto {
    private RegionDto[][] regions;

    public RegionDto[][] getRegions() {
        return regions;
    }

    public void setRegions(RegionDto[][] regions) {
        this.regions = regions;
    }
}
