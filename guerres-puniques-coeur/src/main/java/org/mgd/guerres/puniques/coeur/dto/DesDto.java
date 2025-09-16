package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.List;

public class DesDto extends Dto {
    private List<Integer> valeurs;
    private Integer maximum;
    private Integer valeur;

    public List<Integer> getValeurs() {
        return valeurs;
    }

    public void setValeurs(List<Integer> valeurs) {
        this.valeurs = valeurs;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public Integer getValeur() {
        return valeur;
    }

    public void setValeur(Integer valeur) {
        this.valeur = valeur;
    }
}
