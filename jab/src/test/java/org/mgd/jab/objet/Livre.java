package org.mgd.jab.objet;

import java.util.Map;

public class Livre extends Jo {
    private final Joc<String> nom = new Joc<>(this);
    private final Map<Chapitre, Pegi> chapitres = new JocHashMap<>(this);

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public Map<Chapitre, Pegi> getChapitres() {
        return chapitres;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Livre livre)) return false;
        return nom.idem(livre.nom) && ((JocHashMap<Chapitre, Pegi>) chapitres).idem(livre.chapitres);
    }
}
