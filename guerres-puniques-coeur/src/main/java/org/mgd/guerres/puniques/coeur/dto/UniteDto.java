package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.persistence.PartieJao;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.dto.ReferenceDto;

public class UniteDto extends Dto {
    private ReferenceDto<PartieDto, Partie, PartieJao> type;
    private Integer vie;

    public ReferenceDto<PartieDto, Partie, PartieJao> getType() {
        return type;
    }

    public void setType(ReferenceDto<PartieDto, Partie, PartieJao> type) {
        this.type = type;
    }

    public Integer getVie() {
        return vie;
    }

    public void setVie(Integer vie) {
        this.vie = vie;
    }
}
