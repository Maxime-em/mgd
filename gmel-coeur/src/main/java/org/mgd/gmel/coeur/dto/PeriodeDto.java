package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.Periode;
import org.mgd.jab.dto.Dto;
import org.mgd.temps.LocalRepas;

/**
 * Classe de transformation vers l'objet métier {@link Periode} provenant d'un système de
 * fichiers JSON.
 *
 * @author Maxime
 */
public class PeriodeDto extends Dto {
    private LocalRepas repas;
    private Integer taille;

    public LocalRepas getRepas() {
        return repas;
    }

    public void setRepas(LocalRepas repas) {
        this.repas = repas;
    }

    public Integer getTaille() {
        return taille;
    }

    public void setTaille(Integer taille) {
        this.taille = taille;
    }
}
