package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.List;

public class ReserveDto extends Dto {
    private List<UniteDto> unites;

    public List<UniteDto> getUnites() {
        return unites;
    }

    public void setUnites(List<UniteDto> unites) {
        this.unites = unites;
    }
}
