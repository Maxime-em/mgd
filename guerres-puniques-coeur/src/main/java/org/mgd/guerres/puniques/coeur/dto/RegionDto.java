package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.commun.Alignement;
import org.mgd.guerres.puniques.coeur.commun.TypeRegion;
import org.mgd.jab.dto.Dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegionDto extends Dto {
    private Alignement alignement = Alignement.NEUTRE;
    private List<TypeRegion> types = new ArrayList<>();
    private List<ArmeeDto> armees = Collections.emptyList();

    public Alignement getAlignement() {
        return alignement;
    }

    public void setAlignement(Alignement alignement) {
        this.alignement = alignement;
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
