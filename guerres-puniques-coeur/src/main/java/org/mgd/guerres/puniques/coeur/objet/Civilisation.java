package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.jab.objet.Jo;

import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("java:S2160")
public class Civilisation extends Jo {
    private final Set<Armee> armees = new TreeSet<>();
    private final Map<TypeArmee, Integer> nombresArmeesMaximales = new EnumMap<>(TypeArmee.class);
    private String nom;
    private Reserve reserve;

    public Set<Armee> getArmees() {
        return armees;
    }

    public Map<TypeArmee, Integer> getNombresArmeesMaximales() {
        return nombresArmeesMaximales;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Reserve getReserve() {
        return reserve;
    }

    public void setReserve(Reserve reserve) {
        this.reserve = reserve;
    }

    public String getInformations() {
        return MessageFormat.format("{0} : {1}", nom, reserve.getInformations());
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Civilisation civilisation)) return false;
        return nom.equals(civilisation.nom) && reserve.idem(civilisation.reserve);
    }
}
