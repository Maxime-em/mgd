package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.ArrayList;
import java.util.List;

public class RegionDto extends Dto {
    private List<AlignementDto> alignements = new ArrayList<>();
    private List<TypeRegionDto> types = new ArrayList<>();
    private List<ArmeeDto> armee = new ArrayList<>();
    private List<TransportDto> transports = new ArrayList<>();

    public List<AlignementDto> getAlignements() {
        return alignements;
    }

    public void setAlignements(List<AlignementDto> alignements) {
        this.alignements = alignements;
    }

    public List<TypeRegionDto> getTypes() {
        return types;
    }

    public void setTypes(List<TypeRegionDto> types) {
        this.types = types;
    }

    public List<ArmeeDto> getArmee() {
        return armee;
    }

    public void setArmee(List<ArmeeDto> armee) {
        this.armee = armee;
    }

    public List<TransportDto> getTransports() {
        return transports;
    }

    public void setTransports(List<TransportDto> transports) {
        this.transports = transports;
    }
}
