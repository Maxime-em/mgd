package org.mgd.pam.commun;

public enum PoliceTailleStandard {
    NORMALE(12.0f),
    GRANDE(20.0f);
    private final float valeur;

    PoliceTailleStandard(float valeur) {
        this.valeur = valeur;
    }

    public float getValeur() {
        return valeur;
    }
}
