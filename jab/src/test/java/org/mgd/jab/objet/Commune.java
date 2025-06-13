package org.mgd.jab.objet;

import org.mgd.jab.dto.CommuneDto;
import org.mgd.jab.persistence.CommuneJao;

import java.util.Comparator;

public class Commune extends Jo<CommuneDto> implements Comparable<Commune> {
    private final Joc<String> nom = new Joc<>(this);
    private final Joc<String> code = new Joc<>(this);

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    @Override
    public CommuneDto dto() {
        return new CommuneJao().decharger(this);
    }

    @Override
    public void depuis(CommuneDto dto) {
        setNom(dto.getNom());
        setCode(dto.getCode());
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Commune commune)) return false;
        return nom.idem(commune.nom) && code.idem(commune.code);
    }

    @Override
    public int compareTo(Commune commune) {
        return Comparator.comparing(Commune::getCode).thenComparing(Commune::getNom).compare(this, commune);
    }
}
