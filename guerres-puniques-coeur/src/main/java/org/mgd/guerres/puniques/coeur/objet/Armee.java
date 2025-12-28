package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.jab.objet.Jo;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class Armee extends Jo implements Comparable<Armee> {
    private final Set<Unite> unites = new TreeSet<>();
    private final Set<Alignement> alignements = new TreeSet<>();
    private TypeArmee type;

    public Set<Unite> getUnites() {
        return unites;
    }

    public Set<Alignement> getAlignements() {
        return alignements;
    }

    public TypeArmee getType() {
        return type;
    }

    public void setType(TypeArmee type) {
        this.type = type;
    }

    public String getInformations() {
        return Arrays.stream(TypeUnite.values())
                .map(typeUnite ->
                        MessageFormat.format("{0} {1}",
                                unites.stream().filter(unite -> unite.getType() == typeUnite).count(),
                                typeUnite.getNom()))
                .collect(Collectors.joining(", "));
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
}
