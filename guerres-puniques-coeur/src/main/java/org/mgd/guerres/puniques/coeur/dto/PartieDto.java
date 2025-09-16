package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.objet.Registre;
import org.mgd.guerres.puniques.coeur.persistence.RegistreJao;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.dto.ReferenceDto;

import java.util.List;

public class PartieDto extends Dto {
    private List<CivilisationDto> civilisations;
    private ReferenceDto<RegistreDto, Registre, RegistreJao> informations;
    private MondeDto monde;
    private DesDto desCivilisation;
    private DesDto desActions;

    public List<CivilisationDto> getCivilisations() {
        return civilisations;
    }

    public void setCivilisations(List<CivilisationDto> civilisations) {
        this.civilisations = civilisations;
    }

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

    public DesDto getDesCivilisation() {
        return desCivilisation;
    }

    public void setDesCivilisation(DesDto desCivilisation) {
        this.desCivilisation = desCivilisation;
    }

    public DesDto getDesActions() {
        return desActions;
    }

    public void setDesActions(DesDto desActions) {
        this.desActions = desActions;
    }
}
