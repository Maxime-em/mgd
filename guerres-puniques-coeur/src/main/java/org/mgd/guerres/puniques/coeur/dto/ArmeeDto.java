package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.jab.dto.Dto;

import java.util.List;

public class ArmeeDto extends Dto {
    private List<UniteDto> unites;
    private List<AlignementDto> alignements;
    private TypeArmee type;

    public List<UniteDto> getUnites() {
        return unites;
    }

    public void setUnites(List<UniteDto> unites) {
        this.unites = unites;
    }

    public List<AlignementDto> getAlignements() {
        return alignements;
    }

    public void setAlignements(List<AlignementDto> alignements) {
        this.alignements = alignements;
    }

    public TypeArmee getType() {
        return type;
    }

    public void setType(TypeArmee type) {
        this.type = type;
    }
}
