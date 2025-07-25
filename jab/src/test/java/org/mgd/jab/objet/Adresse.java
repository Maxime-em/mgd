package org.mgd.jab.objet;

import java.util.List;

public class Adresse extends Jo {
    private final List<Personne> proprietaires = new JocArrayList<>(this);
    private final Joc<Voie> voie = new Joc<>(this);
    private final Joc<Commune> commune = new Joc<>(this);
    private final Joc<Pays> pays = new Joc<>(this);

    public List<Personne> getProprietaires() {
        return this.proprietaires;
    }

    public Voie getVoie() {
        return this.voie.get();
    }

    public void setVoie(Voie voie) {
        this.voie.set(voie);
    }

    public Commune getCommune() {
        return commune.get();
    }

    public void setCommune(Commune commune) {
        this.commune.set(commune);
    }

    public Pays getPays() {
        return pays.get();
    }

    public void setPays(Pays pays) {
        this.pays.set(pays);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Adresse adresse)) return false;
        return ((JocArrayList<Personne>) proprietaires).idem(adresse.getProprietaires()) && voie.idem(adresse.voie) && commune.idem(adresse.commune);
    }
}
