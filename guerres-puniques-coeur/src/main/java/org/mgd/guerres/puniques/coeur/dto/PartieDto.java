package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

public class PartieDto extends Dto {
    private MondeDto monde;

    public MondeDto getMonde() {
        return monde;
    }

    public void setMonde(MondeDto monde) {
        this.monde = monde;
    }
}
