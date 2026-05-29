package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.commun.Tabulable;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;

@SuppressWarnings({"java:S2160", "java:S1210"})
public abstract class Type extends Jo implements Comparable<Type>, Tabulable {
    protected final Integer[] texture = new Integer[2];
    protected String nom;
    protected String libelle;
    protected Integer maximum;

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
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof TypeArmee type)) return false;
        return nom.equals(type.nom);
    }

    @Override
    public int compareTo(@NotNull Type type) {
        return Comparator.comparing(Type::getNom).compare(this, type);
    }
}
