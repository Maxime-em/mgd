package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.guerres.puniques.coeur.commun.Posture;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class Alignement extends Jo implements Comparable<Alignement> {
    private Civilisation civilisation;
    private Posture posture;

    public Civilisation getCivilisation() {
        return civilisation;
    }

    public void setCivilisation(Civilisation civilisation) {
        this.civilisation = civilisation;
    }

    public Posture getPosture() {
        return posture;
    }

    public void setPosture(Posture posture) {
        this.posture = posture;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Alignement alignement)) return false;
        return civilisation.idem(alignement.civilisation) && posture == alignement.posture;
    }

    @Override
    public int compareTo(@NotNull Alignement alignement) {
        return Comparator.nullsLast(Comparator.comparing(Alignement::getIdentifiant)).compare(this, alignement);
    }
}
