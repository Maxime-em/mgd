package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.TypeArmeeDto;
import org.mgd.guerres.puniques.coeur.objet.TypeArmee;
import org.mgd.jab.persistence.Jao;

public class TypeArmeeJao extends Jao<TypeArmeeDto, TypeArmee> {
    public TypeArmeeJao() {
        super(TypeArmeeDto.class, TypeArmee.class);
    }

    @Override
    public TypeArmeeDto dto(TypeArmee type) {
        TypeArmeeDto typeArmeeDto = new TypeArmeeDto();
        typeArmeeDto.setTexture(new Integer[]{type.ligne(), type.colonne()});
        typeArmeeDto.setNom(type.getNom());
        typeArmeeDto.setLibelle(type.getLibelle());
        typeArmeeDto.setMaximum(type.getMaximum());

        return typeArmeeDto;
    }

    @Override
    public void enrichir(TypeArmeeDto dto, TypeArmee type) {
        type.ligne(dto.getTexture()[0]);
        type.colonne(dto.getTexture()[1]);
        type.setNom(dto.getNom());
        type.setLibelle(dto.getLibelle());
        type.setMaximum(dto.getMaximum());
    }

    @Override
    protected void copier(TypeArmee source, TypeArmee cible) {
        cible.ligne(source.ligne());
        cible.colonne(source.colonne());
        cible.setNom(source.getNom());
        cible.setLibelle(source.getLibelle());
        cible.setMaximum(source.getMaximum());
    }
}
