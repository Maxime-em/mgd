package org.mgd.guerres.puniques.coeur.objet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("java:S2160")
public class Armee extends Tangible<TypeArmee> {
    private final Set<Unite> unites = new TreeSet<>();
    private final Set<Transport> transports = new TreeSet<>();
    private final Set<Des> desDegats = new HashSet<>();

    public Set<Unite> getUnites() {
        return unites;
    }

    public Set<Transport> getTransports() {
        return transports;
    }

    public Set<Des> getDesDegats() {
        return desDegats;
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
        return unites.equals(armee.unites);
    }
}
