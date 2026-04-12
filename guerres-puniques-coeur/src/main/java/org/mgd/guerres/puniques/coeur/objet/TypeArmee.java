package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.commun.Tabulable;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class TypeArmee extends Jo implements Comparable<TypeArmee>, Tabulable {
    private final Integer[] texture = new Integer[2];
    private String nom;
    private String libelle;
    private Integer maximum;

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

    @Override
    public Integer ligne() {
        return texture[0];
    }

    @Override
    public void ligne(Integer ligne) {
        texture[0] = ligne;
    }

    @Override
    public Integer colonne() {
        return texture[1];
    }

    @Override
    public void colonne(Integer colonne) {
        texture[1] = colonne;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof TypeArmee type)) return false;
        return nom.equals(type.nom);
    }

    @Override
    public int compareTo(@NotNull TypeArmee type) {
        return Comparator.comparing(TypeArmee::getNom).compare(this, type);
    }
}
