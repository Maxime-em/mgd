package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.guerres.puniques.coeur.commun.Alignement;
import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.jab.objet.Jo;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Armee extends Jo {
    private final Set<Unite> unites = new TreeSet<>();
    private TypeArmee type;
    private Alignement alignement;

    public TypeArmee getType() {
        return type;
    }

    public void setType(TypeArmee type) {
        this.type = type;
    }

    public Set<Unite> getUnites() {
        return unites;
    }

    public Alignement getAlignement() {
        return alignement;
    }

    public void setAlignement(Alignement alignement) {
        this.alignement = alignement;
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
        return alignement == armee.alignement && unites.equals(armee.unites);
    }
}
