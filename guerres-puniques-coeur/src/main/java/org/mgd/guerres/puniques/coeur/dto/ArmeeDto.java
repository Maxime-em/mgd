package org.mgd.guerres.puniques.coeur.dto;

import java.util.List;

public class ArmeeDto extends TangibleDto {
    private List<UniteDto> unites;
    private List<DesDto> desDegats;

    public List<UniteDto> getUnites() {
        return unites;
    }

    public void setUnites(List<UniteDto> unites) {
        this.unites = unites;
    }

    public List<DesDto> getDesDegats() {
        return desDegats;
    }

    public void setDesDegats(List<DesDto> desDegats) {
        this.desDegats = desDegats;
    }
}
