package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.Formule;
import org.mgd.jab.dto.Dto;

/**
 * Classe de transformation vers l'objet métier {@link Formule} provenant d'un système de
 * fichiers JSON.
 *
 * @author Maxime
 */
public class FormuleDto extends Dto {
    private RecetteDto recette;
    private PeriodeDto periode;
    private Integer nombreConvives;

    public RecetteDto getRecette() {
        return recette;
    }

    public void setRecette(RecetteDto recette) {
        this.recette = recette;
    }

    public PeriodeDto getPeriode() {
        return periode;
    }

    public void setPeriode(PeriodeDto periode) {
        this.periode = periode;
    }

    public Integer getNombreConvives() {
        return nombreConvives;
    }

    public void setNombreConvives(Integer nombreConvives) {
        this.nombreConvives = nombreConvives;
    }
}
