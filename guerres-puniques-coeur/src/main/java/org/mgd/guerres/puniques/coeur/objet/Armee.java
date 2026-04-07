package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.guerres.puniques.coeur.commun.Posture;
import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class Armee extends Jo implements Comparable<Armee> {
    private final Set<Unite> unites = new TreeSet<>();
    private final Set<Alignement> alignements = new TreeSet<>();
    private final Set<Des> desDegats = new HashSet<>();
    private TypeArmee type;

    public Set<Unite> getUnites() {
        return unites;
    }

    public Set<Alignement> getAlignements() {
        return alignements;
    }

    public Set<Des> getDesDegats() {
        return desDegats;
    }

    public TypeArmee getType() {
        return type;
    }

    public void setType(TypeArmee type) {
        this.type = type;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Armee armee)) return false;
        return unites.equals(armee.unites) && alignements.equals(armee.alignements) && type == armee.type;
    }

    @Override
    public int compareTo(@NotNull Armee armee) {
        return Comparator.comparing(Armee::getType).thenComparing(Armee::getIdentifiant).compare(this, armee);
    }

    public Stream<Civilisation> fluxCivilisationsAmies() {
        return alignements.stream().filter(alignement -> alignement.getPosture() == Posture.AMI).map(Alignement::getCivilisation);
    }
}
