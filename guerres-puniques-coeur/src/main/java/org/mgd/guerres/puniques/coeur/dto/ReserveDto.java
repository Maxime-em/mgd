package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.jab.dto.Dto;

import java.util.List;
import java.util.Map;

public class ReserveDto extends Dto {
    private List<UniteDto> unites;
    private Map<TypeUnite, Integer> nombresUnitesMaximales;

    public List<UniteDto> getUnites() {
        return unites;
    }

    public void setUnites(List<UniteDto> unites) {
        this.unites = unites;
    }

    public Map<TypeUnite, Integer> getNombresUnitesMaximales() {
        return nombresUnitesMaximales;
    }

    public void setNombresUnitesMaximales(Map<TypeUnite, Integer> nombresUnitesMaximales) {
        this.nombresUnitesMaximales = nombresUnitesMaximales;
    }
}
