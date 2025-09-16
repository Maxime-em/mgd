package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.Map;
import java.util.UUID;

public class RegistreDto extends Dto {
    private Map<UUID, InformationsDto> informations;

    public Map<UUID, InformationsDto> getInformations() {
        return informations;
    }

    public void setInformations(Map<UUID, InformationsDto> informations) {
        this.informations = informations;
    }
}
