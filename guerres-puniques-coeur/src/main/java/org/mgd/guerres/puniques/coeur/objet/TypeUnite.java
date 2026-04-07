package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class TypeUnite extends Jo implements Comparable<TypeUnite> {
    private String nom;
    private String libelle;
    private Integer maximum;
    private Integer constitution;
    private Integer force;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public Integer getConstitution() {
        return constitution;
    }

    public void setConstitution(Integer constitution) {
        this.constitution = constitution;
    }

    public Integer getForce() {
        return force;
    }

    public void setForce(Integer force) {
        this.force = force;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof TypeUnite type)) return false;
        return nom.equals(type.nom)
                && Objects.equals(maximum, type.maximum)
                && Objects.equals(force, type.force);
    }

    @Override
    public int compareTo(@NotNull TypeUnite type) {
        return Comparator.comparing(TypeUnite::getNom).compare(this, type);
    }
}
