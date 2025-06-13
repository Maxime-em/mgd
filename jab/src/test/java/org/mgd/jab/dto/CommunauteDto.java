package org.mgd.jab.dto;

import java.util.List;

public class CommunauteDto extends Dto {
    private List<CommuneDto> communes;

    public List<CommuneDto> getCommunes() {
        return communes;
    }

    public void setCommunes(List<CommuneDto> communes) {
        this.communes = communes;
    }
}
