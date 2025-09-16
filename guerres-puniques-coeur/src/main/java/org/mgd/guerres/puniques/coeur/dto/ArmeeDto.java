package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.commun.Alignement;
import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.jab.dto.Dto;

import java.util.List;

public class ArmeeDto extends Dto {
    private TypeArmee type;
    private Alignement alignement;
    private List<UniteDto> unites;

    public TypeArmee getType() {
        return type;
    }

    public void setType(TypeArmee type) {
        this.type = type;
    }

    public Alignement getAlignement() {
        return alignement;
    }

    public void setAlignement(Alignement alignement) {
        this.alignement = alignement;
    }

    public List<UniteDto> getUnites() {
        return unites;
    }

    public void setUnites(List<UniteDto> unites) {
        this.unites = unites;
    }
}
