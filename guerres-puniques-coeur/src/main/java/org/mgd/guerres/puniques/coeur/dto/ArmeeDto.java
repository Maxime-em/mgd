package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.jab.dto.Dto;

import java.util.List;

public class ArmeeDto extends Dto {
    private List<UniteDto> unites;
    private List<AlignementDto> alignements;
    private List<DesDto> desDegats;
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

    public List<DesDto> getDesDegats() {
        return desDegats;
    }

    public void setDesDegats(List<DesDto> desDegats) {
        this.desDegats = desDegats;
    }

    public TypeArmee getType() {
        return type;
    }

    public void setType(TypeArmee type) {
        this.type = type;
    }
}
