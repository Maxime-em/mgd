package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.commun.TypeRegion;
import org.mgd.jab.dto.Dto;

import java.util.ArrayList;
import java.util.List;

public class RegionDto extends Dto {
    private List<AlignementDto> alignements = new ArrayList<>();
    private List<TypeRegion> types = new ArrayList<>();
    private List<ArmeeDto> armees = new ArrayList<>();

    public List<AlignementDto> getAlignements() {
        return alignements;
    }

    public void setAlignements(List<AlignementDto> alignements) {
        this.alignements = alignements;
    }

    public List<TypeRegion> getTypes() {
        return types;
    }

    public void setTypes(List<TypeRegion> types) {
        this.types = types;
    }

    public List<ArmeeDto> getArmees() {
        return armees;
    }

    public void setArmees(List<ArmeeDto> armees) {
        this.armees = armees;
    }
}
