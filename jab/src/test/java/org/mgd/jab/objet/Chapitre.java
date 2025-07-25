package org.mgd.jab.objet;

public class Chapitre extends Jo {
    private final Joc<String> nom = new Joc<>(this);

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Chapitre chapitre)) return false;
        return nom.idem(chapitre.nom);
    }
}
