package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.persistence.PartieJao;
import org.mgd.jab.dto.ReferenceDto;

import java.util.List;

public class TypeUniteDto extends TypeDto {
    private List<ReferenceDto<PartieDto, Partie, PartieJao>> praticables;
    private Integer constitution;
    private Integer force;

    public List<ReferenceDto<PartieDto, Partie, PartieJao>> getPraticables() {
        return praticables;
    }

    public void setPraticables(List<ReferenceDto<PartieDto, Partie, PartieJao>> praticables) {
        this.praticables = praticables;
    }

    public Integer getConstitution() {
        return constitution;
    }

    public void setConstitution(Integer constitution) {
        this.constitution = constitution;
    }

    public Integer getForce() {
        return force;
    }

    public void setForce(Integer force) {
        this.force = force;
    }
}
