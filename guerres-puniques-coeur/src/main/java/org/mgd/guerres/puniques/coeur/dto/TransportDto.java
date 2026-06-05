package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.persistence.PartieJao;
import org.mgd.jab.dto.ReferenceDto;

import java.util.ArrayList;
import java.util.List;

public class TransportDto extends TangibleDto {
    private List<ReferenceDto<PartieDto, Partie, PartieJao>> armees = new ArrayList<>();

    public List<ReferenceDto<PartieDto, Partie, PartieJao>> getArmees() {
        return armees;
    }

    public void setArmees(List<ReferenceDto<PartieDto, Partie, PartieJao>> armees) {
        this.armees = armees;
    }
}
