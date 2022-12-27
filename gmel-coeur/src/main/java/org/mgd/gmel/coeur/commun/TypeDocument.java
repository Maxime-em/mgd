package org.mgd.gmel.coeur.commun;

import java.util.Arrays;

public enum TypeDocument {
    MENUS("Menus"),
    LISTE_DE_COURSES("Liste de courses");

    private final String libelle;

    TypeDocument(String libelle) {
        this.libelle = libelle;
    }

    public static TypeDocument depuisLibelle(String libelle) {
        return Arrays.stream(values()).filter(typeDocument -> typeDocument.getLibelle().equals(libelle)).findFirst().orElseThrow();
    }

    public String getLibelle() {
        return libelle;
    }
}
