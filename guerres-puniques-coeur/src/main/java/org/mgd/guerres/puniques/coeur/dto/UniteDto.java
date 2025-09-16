package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.jab.dto.Dto;

public class UniteDto extends Dto {
    private TypeUnite type;
    private Integer vie;

    public TypeUnite getType() {
        return type;
    }

    public void setType(TypeUnite type) {
        this.type = type;
    }

    public Integer getVie() {
        return vie;
    }

    public void setVie(Integer vie) {
        this.vie = vie;
    }
}
