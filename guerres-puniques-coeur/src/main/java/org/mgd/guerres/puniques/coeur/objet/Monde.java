package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.utilitaire.Jos;

import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings("java:S2160")
public class Monde extends Jo {
    private Region[][] regions;

    public Region[][] getRegions() {
        return regions;
    }

    public void setRegions(Region[][] regions) {
        this.regions = regions;
    }

    public Region getRegion(int ligne, int colonne) {
        return regions[ligne][colonne];
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
