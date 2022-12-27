package org.mgd.jab.objet;

import org.mgd.jab.dto.ChapitreDto;
import org.mgd.jab.persistence.ChapitreJao;

public class Chapitre extends Jo<ChapitreDto> {
    private final Joc<String> nom = new Joc<>(this);

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    @Override
    public ChapitreDto dto() {
        return new ChapitreJao().decharger(this);
    }

    @Override
    public void depuis(ChapitreDto dto) {
        setNom(dto.getNom());
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Chapitre chapitre)) return false;
        return nom.idem(chapitre.nom);
    }
}
