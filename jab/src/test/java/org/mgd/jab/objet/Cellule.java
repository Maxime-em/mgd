package org.mgd.jab.objet;

import org.mgd.commun.Tabulable;

/**
 * Cellule de l'asile d'Arkham occupée par un vilain, objet métier tabulable de test.
 */
public class Cellule extends Jo implements Tabulable {
    private String occupant;
    private Integer ligne;
    private Integer colonne;

    public Cellule() {
        super();
    }

    public String getOccupant() {
        return occupant;
    }

    public void setOccupant(String occupant) {
        this.occupant = occupant;
    }

    @Override
    public Integer ligne() {
        return ligne;
    }

    @Override
    public void ligne(Integer ligne) {
        this.ligne = ligne;
    }

    @Override
    public Integer colonne() {
        return colonne;
    }

    @Override
    public void colonne(Integer colonne) {
        this.colonne = colonne;
    }

    @Override
    public boolean idem(Object objet) {
        return objet instanceof Cellule cellule && java.util.Objects.equals(occupant, cellule.occupant);
    }
}
