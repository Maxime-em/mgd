package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.TypeTransportDto;
import org.mgd.guerres.puniques.coeur.objet.TypeTransport;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class TypeTransportJao extends Jao<TypeTransportDto, TypeTransport> {
    public TypeTransportJao() {
        super(TypeTransportDto.class, TypeTransport.class);
    }

    @Override
    public TypeTransportDto dto(TypeTransport type) {
        TypeTransportDto typeTransportDto = new TypeTransportDto();
        typeTransportDto.setPraticables(new TypeRegionJao().decharger(type.getPraticables()));
        typeTransportDto.setTexture(new Integer[]{type.ligne(), type.colonne()});
        typeTransportDto.setNom(type.getNom());
        typeTransportDto.setLibelle(type.getLibelle());
        typeTransportDto.setMaximum(type.getMaximum());

        return typeTransportDto;
    }

    @Override
    public void enrichir(TypeTransportDto dto, TypeTransport type) throws JaoExecutionException, JaoParseException {
        type.getPraticables().addAll(new TypeRegionJao().charger(dto.getPraticables(), type));
        type.ligne(dto.getTexture()[0]);
        type.colonne(dto.getTexture()[1]);
        type.setNom(dto.getNom());
        type.setLibelle(dto.getLibelle());
        type.setMaximum(dto.getMaximum());
    }

    @Override
    protected void copier(TypeTransport source, TypeTransport cible) throws JaoExecutionException, JaoParseException {
        cible.getPraticables().clear();
        cible.getPraticables().addAll(new TypeRegionJao().dupliquer(source.getPraticables()));
        cible.ligne(source.ligne());
        cible.colonne(source.colonne());
        cible.setNom(source.getNom());
        cible.setLibelle(source.getLibelle());
        cible.setMaximum(source.getMaximum());
    }
}
