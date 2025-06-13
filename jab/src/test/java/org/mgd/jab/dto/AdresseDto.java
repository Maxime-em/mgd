package org.mgd.jab.dto;

import org.mgd.jab.objet.Communaute;
import org.mgd.jab.objet.Monde;
import org.mgd.jab.objet.Personne;
import org.mgd.jab.persistence.CommunauteJao;
import org.mgd.jab.persistence.MondeJao;
import org.mgd.jab.persistence.PersonneJao;

import java.util.List;

public class AdresseDto extends Dto {
    private List<ReferenceDto<PersonneDto, Personne, PersonneJao>> proprietaires;
    private VoieDto voie;
    private ReferenceDto<CommunauteDto, Communaute, CommunauteJao> commune;
    private ReferenceDto<MondeDto, Monde, MondeJao> pays;

    public List<ReferenceDto<PersonneDto, Personne, PersonneJao>> getProprietaires() {
        return proprietaires;
    }

    public void setProprietaires(List<ReferenceDto<PersonneDto, Personne, PersonneJao>> proprietaires) {
        this.proprietaires = proprietaires;
    }

    public VoieDto getVoie() {
        return voie;
    }

    public void setVoie(VoieDto voie) {
        this.voie = voie;
    }

    public ReferenceDto<CommunauteDto, Communaute, CommunauteJao> getCommune() {
        return commune;
    }

    public void setCommune(ReferenceDto<CommunauteDto, Communaute, CommunauteJao> commune) {
        this.commune = commune;
    }

    public ReferenceDto<MondeDto, Monde, MondeJao> getPays() {
        return pays;
    }

    public void setPays(ReferenceDto<MondeDto, Monde, MondeJao> pays) {
        this.pays = pays;
    }
}
