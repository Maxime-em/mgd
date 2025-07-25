package org.mgd.jab.objet;

import java.util.Objects;

public class Voie extends Jo {
    private final Joc<Integer> numero = new Joc<>(this);
    private final Joc<String> libelle = new Joc<>(this);

    public Integer getNumero() {
        return this.numero.get();
    }

    public void setNumero(Integer numero) {
        this.numero.set(numero);
    }

    public String getLibelle() {
        return this.libelle.get();
    }

    public void setLibelle(String libelle) {
        this.libelle.set(libelle);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Voie voie)) return false;
        return Objects.equals(numero, voie.numero) && Objects.equals(libelle, voie.libelle);
    }
}
