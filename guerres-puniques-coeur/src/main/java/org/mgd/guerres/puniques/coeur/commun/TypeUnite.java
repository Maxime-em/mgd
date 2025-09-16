package org.mgd.guerres.puniques.coeur.commun;

public enum TypeUnite {
    SOLDAT("soldats", "Ajouter un soldat", 3),
    GENERAL("généraux", "Ajouter un général", 12),
    ELEPHANT("éléphants", "Ajouter un éléphant", 20),
    CATAPULTE("catapultes", "Ajouter une catapulte", 20);

    private final String nom;
    private final String libelle;
    private final Integer vie;

    TypeUnite(String nom, String libelle, Integer vie) {
        this.nom = nom;
        this.libelle = libelle;
        this.vie = vie;
    }

    public String getNom() {
        return nom;
    }

    public Integer getVie() {
        return vie;
    }

    public String getLibelle() {
        return libelle;
    }
}
