package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.persistence.PartieJao;
import org.mgd.jab.dto.ReferenceDto;

import java.util.List;

public class TypeTransportDto extends TypeDto {
    private List<ReferenceDto<PartieDto, Partie, PartieJao>> praticables;

    public List<ReferenceDto<PartieDto, Partie, PartieJao>> getPraticables() {
        return praticables;
    }

    public void setPraticables(List<ReferenceDto<PartieDto, Partie, PartieJao>> praticables) {
        this.praticables = praticables;
    }
}
