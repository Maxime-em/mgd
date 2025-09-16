package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.Arrays;
import java.util.stream.Stream;

public class MondeDto extends Dto {
    private RegionDto[][] regions;

    public RegionDto[][] getRegions() {
        return regions;
    }

    public void setRegions(RegionDto[][] regions) {
        this.regions = regions;
    }

    public Stream<RegionDto> fluxRegions() {
        return Arrays.stream(regions).flatMap(Arrays::stream);
    }
}
