package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.objet.Registre;
import org.mgd.guerres.puniques.coeur.persistence.RegistreJao;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.dto.ReferenceDto;

import java.util.List;

public class PartieDto extends Dto {
    private ReferenceDto<RegistreDto, Registre, RegistreJao> informations;
    private MondeDto monde;
    private List<CivilisationDto> civilisations;

    public ReferenceDto<RegistreDto, Registre, RegistreJao> getInformations() {
        return informations;
    }

    public void setInformations(ReferenceDto<RegistreDto, Registre, RegistreJao> informations) {
        this.informations = informations;
    }

    public MondeDto getMonde() {
        return monde;
    }

    public void setMonde(MondeDto monde) {
        this.monde = monde;
    }

    public List<CivilisationDto> getCivilisations() {
        return civilisations;
    }

    public void setCivilisations(List<CivilisationDto> civilisations) {
        this.civilisations = civilisations;
    }
}
