package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.commun.Posture;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.persistence.PartieJao;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.dto.ReferenceDto;

public class AlignementDto extends Dto {
    private ReferenceDto<PartieDto, Partie, PartieJao> civilisation;
    private Posture posture;

    public ReferenceDto<PartieDto, Partie, PartieJao> getCivilisation() {
        return civilisation;
    }

    public void setCivilisation(ReferenceDto<PartieDto, Partie, PartieJao> civilisation) {
        this.civilisation = civilisation;
    }

    public Posture getPosture() {
        return posture;
    }

    public void setPosture(Posture posture) {
        this.posture = posture;
    }
}
