package org.mgd.guerres.puniques.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.List;

public class CivilisationDto extends Dto {
    private List<TypeUniteDto> typesUnites;
    private List<TypeTransportDto> typesTransports;
    private List<TypeArmeeDto> typeArmees;
    private List<TransportDto> transports;
    private List<ArmeeDto> armees;
    private String nom;
    private ReserveDto reserve;

    public List<TypeUniteDto> getTypesUnites() {
        return typesUnites;
    }

    public void setTypesUnites(List<TypeUniteDto> typesUnites) {
        this.typesUnites = typesUnites;
    }

    public List<TypeTransportDto> getTypesTransports() {
        return typesTransports;
    }

    public void setTypesTransports(List<TypeTransportDto> typesTransports) {
        this.typesTransports = typesTransports;
    }

    public List<TransportDto> getTransports() {
        return transports;
    }

    public void setTransports(List<TransportDto> transports) {
        this.transports = transports;
    }

    public List<TypeArmeeDto> getTypeArmees() {
        return typeArmees;
    }

    public void setTypeArmees(List<TypeArmeeDto> typeArmees) {
        this.typeArmees = typeArmees;
    }

    public List<ArmeeDto> getArmees() {
        return armees;
    }

    public void setArmees(List<ArmeeDto> armees) {
        this.armees = armees;
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
