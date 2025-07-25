package org.mgd.jab.objet;

import org.mgd.jab.persistence.JeuType;

public class Jeu extends Jo {
    private final Joc<JeuType> type = new Joc<>(this);
    private final Joc<String> nom = new Joc<>(this);
    private final Joc<Integer> annee = new Joc<>(this);
    private final Joc<Integer> semaine = new Joc<>(this);
    private final Joc<Pegi> pegi = new Joc<>(this);

    public JeuType getType() {
        return type.get();
    }

    public void setType(JeuType type) {
        this.type.set(type);
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public Integer getAnnee() {
        return this.annee.get();
    }

    public void setAnnee(Integer annee) {
        this.annee.set(annee);
    }

    public Integer getSemaine() {
        return this.semaine.get();
    }

    public void setSemaine(Integer semaine) {
        this.semaine.set(semaine);
    }

    public Pegi getPegi() {
        return this.pegi.get();
    }

    public void setPegi(Pegi pegi) {
        this.pegi.set(pegi);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Jeu jeu)) return false;
        return type.idem(jeu.type) && nom.idem(jeu.nom) && annee.idem(jeu.annee) && semaine.idem(jeu.semaine) && pegi.idem(jeu.pegi);
    }
}
