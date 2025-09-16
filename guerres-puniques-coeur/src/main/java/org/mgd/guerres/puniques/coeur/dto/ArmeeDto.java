package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.commun.Alignement;
import org.mgd.jab.dto.Dto;

import java.util.List;

public class ArmeeDto extends Dto {
    private Alignement alignement;
    private List<UniteDto> unites;

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
