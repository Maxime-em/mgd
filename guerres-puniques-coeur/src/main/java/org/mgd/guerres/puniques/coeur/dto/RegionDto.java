package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.persistence.PartieJao;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.dto.ReferenceDto;

import java.util.ArrayList;
import java.util.List;

public class RegionDto extends Dto {
    private List<AlignementDto> alignements = new ArrayList<>();
    private List<ReferenceDto<PartieDto, Partie, PartieJao>> types = new ArrayList<>();
    private List<ReferenceDto<PartieDto, Partie, PartieJao>> armee = new ArrayList<>();
    private List<ReferenceDto<PartieDto, Partie, PartieJao>> transports = new ArrayList<>();

    public List<AlignementDto> getAlignements() {
        return alignements;
    }

    public void setAlignements(List<AlignementDto> alignements) {
        this.alignements = alignements;
    }

    public List<ReferenceDto<PartieDto, Partie, PartieJao>> getTypes() {
        return types;
    }

    public void setTypes(List<ReferenceDto<PartieDto, Partie, PartieJao>> types) {
        this.types = types;
    }

    public List<ReferenceDto<PartieDto, Partie, PartieJao>> getArmee() {
        return armee;
    }

    public void setArmee(List<ReferenceDto<PartieDto, Partie, PartieJao>> armee) {
        this.armee = armee;
    }

    public List<ReferenceDto<PartieDto, Partie, PartieJao>> getTransports() {
        return transports;
    }

    public void setTransports(List<ReferenceDto<PartieDto, Partie, PartieJao>> transports) {
        this.transports = transports;
    }
}
