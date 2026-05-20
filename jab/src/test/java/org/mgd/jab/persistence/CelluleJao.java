package org.mgd.jab.persistence;

import org.mgd.jab.dto.CelluleDto;
import org.mgd.jab.objet.Cellule;

/**
 * Accès aux données des cellules d'Arkham, objet de test.
 */
public class CelluleJao extends Jao<CelluleDto, Cellule> {
    public CelluleJao() {
        super(CelluleDto.class, Cellule.class);
    }

    @Override
    public CelluleDto dto(Cellule objet) {
        CelluleDto dto = new CelluleDto();
        dto.occupant = objet.getOccupant();
        return dto;
    }

    @Override
    public void enrichir(CelluleDto dto, Cellule objet) {
        objet.setOccupant(dto.occupant);
    }

    @Override
    protected void copier(Cellule source, Cellule cible) {
        cible.setOccupant(source.getOccupant());
    }
}
