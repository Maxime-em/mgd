package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.jab.objet.Jo;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class Civilisation extends Jo implements Comparable<Civilisation> {
    private final Set<TypeUnite> typesUnites = new TreeSet<>();
    private final Set<TypeTransport> typesTransports = new TreeSet<>();
    private final Set<TypeArmee> typeArmees = new TreeSet<>();
    private final Set<Transport> transports = new TreeSet<>();
    private final Set<Armee> armees = new TreeSet<>();
    private String nom;
    private Reserve reserve;
    private Region capitale;

    @SuppressWarnings("unchecked")
    public <T extends Type, O extends Tangible<T>> Collection<O> getTangible(T type) {
        return (Collection<O>) switch (type) {
            case TypeArmee _ -> armees;
            case TypeTransport _ -> transports;
            case TypeUnite _ -> reserve.getUnites();
            default -> throw new IllegalStateException(MessageFormat.format("Le type {0} est inconnu", type));
        };
    }

    public Set<TypeUnite> getTypesUnites() {
        return typesUnites;
    }

    public Set<TypeTransport> getTypesTransports() {
        return typesTransports;
    }

    public Set<Transport> getTransports() {
        return transports;
    }

    public Set<TypeArmee> getTypeArmees() {
        return typeArmees;
    }

    public Set<Armee> getArmees() {
        return armees;
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

    public Region getCapitale() {
        return this.capitale;
    }

    public void setCapitale(Region capitale) {
        this.capitale = capitale;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Civilisation civilisation)) return false;
        return nom.equals(civilisation.nom);
    }

    @Override
    public int compareTo(@NotNull Civilisation civilisation) {
        return Comparator.comparing(Civilisation::getNom).compare(this, civilisation);
    }
}
