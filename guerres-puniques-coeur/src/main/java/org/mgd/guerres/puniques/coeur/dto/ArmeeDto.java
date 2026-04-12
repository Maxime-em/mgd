package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.List;

public class ArmeeDto extends Dto {
    private List<UniteDto> unites;
    private List<AlignementDto> alignements;
    private List<TransportDto> transports;
    private List<DesDto> desDegats;
    private TypeArmeeDto type;

    public List<UniteDto> getUnites() {
        return unites;
    }

    public void setUnites(List<UniteDto> unites) {
        this.unites = unites;
    }

    public List<AlignementDto> getAlignements() {
        return alignements;
    }

    public void setAlignements(List<AlignementDto> alignements) {
        this.alignements = alignements;
    }

    public List<TransportDto> getTransports() {
        return transports;
    }

    public void setTransports(List<TransportDto> transports) {
        this.transports = transports;
    }

    public List<DesDto> getDesDegats() {
        return desDegats;
    }

    public void setDesDegats(List<DesDto> desDegats) {
        this.desDegats = desDegats;
    }

    public TypeArmeeDto getType() {
        return type;
    }

    public void setType(TypeArmeeDto type) {
        this.type = type;
    }
}
