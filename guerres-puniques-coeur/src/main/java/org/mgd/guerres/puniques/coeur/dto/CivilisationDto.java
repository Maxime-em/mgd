package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.jab.dto.Dto;

import java.util.List;
import java.util.Map;

public class CivilisationDto extends Dto {
    private List<ArmeeDto> armees;
    private Map<TypeArmee, Integer> nombresArmeesMaximales;
    private String nom;
    private ReserveDto reserve;

    public List<ArmeeDto> getArmees() {
        return armees;
    }

    public void setArmees(List<ArmeeDto> armees) {
        this.armees = armees;
    }

    public Map<TypeArmee, Integer> getNombresArmeesMaximales() {
        return nombresArmeesMaximales;
    }

    public void setNombresArmeesMaximales(Map<TypeArmee, Integer> nombresArmeesMaximales) {
        this.nombresArmeesMaximales = nombresArmeesMaximales;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public ReserveDto getReserve() {
        return reserve;
    }

    public void setReserve(ReserveDto reserve) {
        this.reserve = reserve;
    }
}
