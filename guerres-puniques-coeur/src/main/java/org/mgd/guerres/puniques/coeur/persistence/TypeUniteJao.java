package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.TypeUniteDto;
import org.mgd.guerres.puniques.coeur.objet.TypeUnite;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class TypeUniteJao extends Jao<TypeUniteDto, TypeUnite> {
    public TypeUniteJao() {
        super(TypeUniteDto.class, TypeUnite.class);
    }

    @Override
    public TypeUniteDto dto(TypeUnite typeUnite) {
        TypeUniteDto typeUniteDto = new TypeUniteDto();
        typeUniteDto.setPraticables(new TypeRegionJao().decharger(typeUnite.getPraticables()));
        typeUniteDto.setNom(typeUnite.getNom());
        typeUniteDto.setLibelle(typeUnite.getLibelle());
        typeUniteDto.setMaximum(typeUnite.getMaximum());
        typeUniteDto.setConstitution(typeUnite.getConstitution());
        typeUniteDto.setForce(typeUnite.getForce());

        return typeUniteDto;
    }

    @Override
    public void enrichir(TypeUniteDto dto, TypeUnite typeUnite) throws JaoExecutionException, JaoParseException {
        typeUnite.getPraticables().addAll(new TypeRegionJao().charger(dto.getPraticables(), typeUnite));
        typeUnite.setNom(dto.getNom());
        typeUnite.setLibelle(dto.getLibelle());
        typeUnite.setMaximum(dto.getMaximum());
        typeUnite.setConstitution(dto.getConstitution());
        typeUnite.setForce(dto.getForce());
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
