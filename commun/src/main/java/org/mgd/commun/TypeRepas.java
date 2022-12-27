package org.mgd.commun;

public enum TypeRepas {
    @EntryPoint
    PETIT_DEJEUNER("Petit-déjeuner"),
    @EntryPoint
    DEJEUNER("Déjeuner"),
    @EntryPoint
    DINER("Dîner");
    private final String label;

    TypeRepas(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
