package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.jab.objet.Jo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class Armee extends Jo implements Comparable<Armee>, Typable {
    private final Set<Unite> unites = new TreeSet<>();
    private final Set<Alignement> alignements = new TreeSet<>();
    private final Set<Transport> transports = new TreeSet<>();
    private final Set<Des> desDegats = new HashSet<>();
    private TypeArmee type;

    public Set<Unite> getUnites() {
        return unites;
    }

    public Set<Alignement> getAlignements() {
        return alignements;
    }

    public Set<Transport> getTransports() {
        return transports;
    }

    public Set<Des> getDesDegats() {
        return desDegats;
    }

    @Override
    public TypeArmee getType() {
        return type;
    }

    public void setType(TypeArmee type) {
        this.type = type;
    }

    public Set<TypeRegion> getPraticables() {
        return Stream.concat(unites.stream()
                                .map(unite -> unite.getType().getPraticables())
                                .reduce((intersection, praticables) -> intersection.stream().filter(praticables::contains).collect(Collectors.toSet()))
                                .stream()
                                .flatMap(Collection::stream),
                        transports.stream().flatMap(transport -> transport.getType().getPraticables().stream()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Armee armee)) return false;
        return unites.equals(armee.unites) && alignements.equals(armee.alignements);
    }

    @Override
    public int compareTo(@NotNull Armee armee) {
        return Comparator.comparing(Armee::getIdentifiant).compare(this, armee);
    }
}
