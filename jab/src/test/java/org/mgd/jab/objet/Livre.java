package org.mgd.jab.objet;

import org.mgd.jab.dto.LivreDto;
import org.mgd.jab.persistence.ChapitreJao;
import org.mgd.jab.persistence.LivreJao;
import org.mgd.jab.persistence.PegiJao;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.Map;

public class Livre extends Jo<LivreDto> {
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
    public LivreDto dto() {
        return new LivreJao().decharger(this);
    }

    @Override
    public void depuis(LivreDto dto) throws JaoParseException, VerificationException {
        Verifications.nonNull(dto.getChapitres(), "La liste des chapitres d''un livre devrait être une liste éventuellement vide");

        setNom(dto.getNom());
        getChapitres().putAll(new ChapitreJao().charger(new PegiJao(), dto.getChapitres(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Livre livre)) return false;
        return nom.idem(livre.nom) && ((JocHashMap<Chapitre, Pegi>) chapitres).idem(livre.chapitres);
    }
}
