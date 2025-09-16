package org.mgd.guerres.puniques.coeur.commun;

public enum TypeUnite {
    SOLDAT("soldats", 3),
    GENERAL("généraux", 12),
    ELEPHANT("éléphants", 20),
    CATAPULTE("catapultes", 20);

    private final String nom;
    private final Integer vie;

    TypeUnite(String nom, Integer vie) {
        this.nom = nom;
        this.vie = vie;
    }

    public String getNom() {
        return nom;
    }

    public Integer getVie() {
        return vie;
    }
}
