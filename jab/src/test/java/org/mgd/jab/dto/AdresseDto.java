package org.mgd.jab.dto;

import java.util.List;

public class AdresseDto extends Dto {
    private List<PersonneDto> proprietaires;
    private VoieDto voie;

    public List<PersonneDto> getProprietaires() {
        return proprietaires;
    }

    public void setProprietaires(List<PersonneDto> proprietaires) {
        this.proprietaires = proprietaires;
    }

    public VoieDto getVoie() {
        return voie;
    }

    public void setVoie(VoieDto voie) {
        this.voie = voie;
    }
}
