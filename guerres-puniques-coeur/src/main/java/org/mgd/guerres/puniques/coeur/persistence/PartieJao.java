package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.PartieDto;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.objet.Registre;
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
        partieDto.setInformations(new InformationsJao().dechargerVersReference(partie.getInformations(), Registre.class, RegistreJao.class));
        partieDto.setMonde(new MondeJao().decharger(partie.getMonde()));
        partieDto.setCivilisations(new CivilisationJao().decharger(partie.getCivilisations()));

        return partieDto;
    }

    @Override
    public void enrichir(PartieDto dto, Partie partie) throws JaoExecutionException, JaoParseException {
        partie.setInformations(new InformationsJao().chargerParReference(dto.getInformations()));
        partie.setMonde(new MondeJao().charger(dto.getMonde(), partie));
        partie.getCivilisations().addAll(new CivilisationJao().charger(dto.getCivilisations(), partie));
    }

    @Override
    protected void copier(Partie source, Partie cible) throws JaoExecutionException, JaoParseException {
        cible.setInformations(new InformationsJao().dupliquer(source.getInformations()));
        cible.setMonde(new MondeJao().dupliquer(source.getMonde()));
        cible.getCivilisations().clear();
        cible.getCivilisations().addAll(new CivilisationJao().dupliquer(source.getCivilisations()));
    }
}
