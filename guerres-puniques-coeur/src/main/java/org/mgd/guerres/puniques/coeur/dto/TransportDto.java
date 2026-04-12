package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

public class TransportDto extends Dto {
    private TypeTransportDto type;

    public TypeTransportDto getType() {
        return type;
    }

    public void setType(TypeTransportDto type) {
        this.type = type;
    }
}
