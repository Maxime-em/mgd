package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.persistence.PartieJao;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.dto.ReferenceDto;

public abstract class TangibleDto extends Dto {
    private ReferenceDto<PartieDto, Partie, PartieJao> type;
    private ReferenceDto<PartieDto, Partie, PartieJao> origine;

    public ReferenceDto<PartieDto, Partie, PartieJao> getType() {
        return type;
    }

    public void setType(ReferenceDto<PartieDto, Partie, PartieJao> type) {
        this.type = type;
    }

    public ReferenceDto<PartieDto, Partie, PartieJao> getOrigine() {
        return origine;
    }

    public void setOrigine(ReferenceDto<PartieDto, Partie, PartieJao> origine) {
        this.origine = origine;
    }
}
