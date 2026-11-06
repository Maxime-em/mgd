package org.mgd.jab.persistence;

import org.mgd.jab.dto.BatmobileDto;
import org.mgd.jab.objet.Batmobile;

/**
 * Accès aux données de la Batmobile : l'objet n'a pas de constructeur sans argument, son instanciation échoue.
 */
public class BatmobileJao extends Jao<BatmobileDto, Batmobile> {
    public BatmobileJao() {
        super(BatmobileDto.class, Batmobile.class);
    }

    @Override
    public BatmobileDto dto(Batmobile objet) {
        BatmobileDto dto = new BatmobileDto();
        dto.modele = objet.getModele();
        return dto;
    }

    @Override
    public void enrichir(BatmobileDto dto, Batmobile objet) {
        // Rien à enrichir.
    }

    @Override
    protected void copier(Batmobile source, Batmobile cible) {
        // Rien à copier.
    }
}
