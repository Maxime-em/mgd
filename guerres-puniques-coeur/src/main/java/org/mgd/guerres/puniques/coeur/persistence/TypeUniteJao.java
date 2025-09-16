package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.TypeUniteDto;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.guerres.puniques.coeur.objet.TypeUnite;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class TypeUniteJao extends Jao<TypeUniteDto, TypeUnite> {
    public TypeUniteJao() {
        super(TypeUniteDto.class, TypeUnite.class);
    }

    @Override
    public TypeUniteDto dto(TypeUnite type) {
        TypeUniteDto typeUniteDto = new TypeUniteDto();
        typeUniteDto.setPraticables(new TypeRegionJao().dechargerVersReferences(type.getPraticables(), Partie.class, PartieJao.class));
        typeUniteDto.setNom(type.getNom());
        typeUniteDto.setLibelle(type.getLibelle());
        typeUniteDto.setMaximum(type.getMaximum());
        typeUniteDto.setConstitution(type.getConstitution());
        typeUniteDto.setForce(type.getForce());

        return typeUniteDto;
    }

    @Override
    public void enrichir(TypeUniteDto dto, TypeUnite type) throws JaoExecutionException, JaoParseException {
        type.setNom(dto.getNom());
        type.setLibelle(dto.getLibelle());
        type.setMaximum(dto.getMaximum());
        type.setConstitution(dto.getConstitution());
        type.setForce(dto.getForce());

        postChargement(type, objet -> objet.getPraticables().addAll(new TypeRegionJao().chargerParReferences(dto.getPraticables())));
    }

    @Override
    protected void copier(TypeUnite source, TypeUnite cible) throws JaoExecutionException, JaoParseException {
        cible.getPraticables().clear();
        cible.getPraticables().addAll(new TypeRegionJao().dupliquer(source.getPraticables()));
        cible.setNom(source.getNom());
        cible.setLibelle(source.getLibelle());
        cible.setMaximum(source.getMaximum());
        cible.setConstitution(source.getConstitution());
        cible.setForce(source.getForce());
    }
}
