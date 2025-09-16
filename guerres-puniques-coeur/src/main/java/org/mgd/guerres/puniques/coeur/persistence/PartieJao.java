package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.PartieDto;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class PartieJao extends Jao<PartieDto, Partie> {
    public PartieJao() {
        super(PartieDto.class, Partie.class);
    }

    @Override
    public PartieDto dto(Partie partie) {
        PartieDto partieDto = new PartieDto();
        partieDto.setMonde(new MondeJao().decharger(partie.getMonde()));

        return partieDto;
    }

    @Override
    public void enrichir(PartieDto dto, Partie partie) throws JaoExecutionException, JaoParseException {
        partie.setMonde(new MondeJao().charger(dto.getMonde(), partie));
    }

    @Override
    protected void copier(Partie source, Partie cible) throws JaoExecutionException, JaoParseException {
        cible.setMonde(new MondeJao().dupliquer(source.getMonde()));
    }
}
