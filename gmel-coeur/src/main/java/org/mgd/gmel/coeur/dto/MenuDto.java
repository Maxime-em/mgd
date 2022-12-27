package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.jab.dto.Dto;

import java.util.List;

/**
 * Classe de transformation vers l'objet métier {@link Menu} provenant d'un système de
 * fichiers JSON.
 *
 * @author Maxime
 */
public class MenuDto extends Dto {
    private Integer annee;
    private Integer semaine;
    private List<FormuleDto> formules;

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Integer getSemaine() {
        return semaine;
    }

    public void setSemaine(Integer semaine) {
        this.semaine = semaine;
    }

    public List<FormuleDto> getFormules() {
        return formules;
    }

    public void setFormules(List<FormuleDto> formules) {
        this.formules = formules;
    }
}
