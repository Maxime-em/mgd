package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.utilitaire.Jos;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("java:S2160")
public class Monde extends Jo {
    private final Set<TypeRegion> types = new TreeSet<>();
    private Region[][] regions;

    public Set<TypeRegion> getTypes() {
        return types;
    }

    public Set<TypeRegion> getTypes(String codes) {
        return types.stream().filter(type -> codes.contains(type.getCode())).collect(Collectors.toSet());
    }

    public Region[][] getRegions() {
        return regions;
    }

    public void setRegions(Region[][] regions) {
        this.regions = regions;
    }

    public Region getRegion(int ligne, int colonne) {
        return regions[ligne][colonne];
    }

    public Region getRegion(Integer[] index) {
        return getRegion(index[0], index[1]);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Monde monde)) return false;
        return Jos.idem(regions, monde.regions);
    }

    public Stream<Region> fluxRegions() {
        return Arrays.stream(regions).flatMap(Arrays::stream);
    }
}
