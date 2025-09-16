package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.AlignementDto;
import org.mgd.guerres.puniques.coeur.objet.Alignement;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class AlignementJao extends Jao<AlignementDto, Alignement> {
    public AlignementJao() {
        super(AlignementDto.class, Alignement.class);
    }

    @Override
    public AlignementDto dto(Alignement alignement) {
        AlignementDto alignementDto = new AlignementDto();
        alignementDto.setCivilisation(new CivilisationJao().dechargerVersReference(alignement.getCivilisation(), Partie.class, PartieJao.class));
        alignementDto.setPosture(alignement.getPosture());
        return alignementDto;
    }

    @Override
    public void enrichir(AlignementDto dto, Alignement alignement) {
        alignement.setPosture(dto.getPosture());
    }

    @Override
    protected void copier(Alignement source, Alignement cible) throws JaoExecutionException, JaoParseException {
        cible.setCivilisation(new CivilisationJao().dupliquer(source.getCivilisation()));
        cible.setPosture(source.getPosture());
    }
}
