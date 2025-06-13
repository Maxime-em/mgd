package org.mgd.jab.objet;

import org.mgd.jab.dto.PaysDto;
import org.mgd.jab.persistence.PaysJao;

import java.util.Comparator;

public class Pays extends Jo<PaysDto> implements Comparable<Pays> {
    private final Joc<String> nom = new Joc<>(this);

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    @Override
    public PaysDto dto() {
        return new PaysJao().decharger(this);
    }

    @Override
    public void depuis(PaysDto dto) {
        setNom(dto.getNom());
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Pays pays)) return false;
        return nom.idem(pays.nom);
    }

    @Override
    public int compareTo(Pays pays) {
        return Comparator.comparing(Pays::getNom).compare(this, pays);
    }
}
