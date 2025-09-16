package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.UniteDto;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.objet.Unite;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class UniteJao extends Jao<UniteDto, Unite> {
    public UniteJao() {
        super(UniteDto.class, Unite.class);
    }

    @Override
    public UniteDto dto(Unite unite) {
        UniteDto uniteDto = new UniteDto();
        uniteDto.setType(new TypeUniteJao().dechargerVersReference(unite.getType(), Partie.class, PartieJao.class));
        uniteDto.setOrigine(new CivilisationJao().dechargerVersReference(unite.getOrigine(), Partie.class, PartieJao.class));
        uniteDto.setVie(unite.getVie());

        return uniteDto;
    }

    @Override
    public void enrichir(UniteDto dto, Unite unite) throws JaoExecutionException, JaoParseException {
        unite.setVie(dto.getVie());

        postChargement(unite, objet -> {
            objet.setType(new TypeUniteJao().chargerParReference(dto.getType()));
            objet.setOrigine(new CivilisationJao().chargerParReference(dto.getOrigine()));
        });
    }

    @Override
    protected void copier(Unite source, Unite cible) throws JaoExecutionException, JaoParseException {
        cible.setType(new TypeUniteJao().dupliquer(source.getType()));
        cible.setOrigine(new CivilisationJao().dupliquer(source.getOrigine()));
        cible.setVie(source.getVie());
    }
}
