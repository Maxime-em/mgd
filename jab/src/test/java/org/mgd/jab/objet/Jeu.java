package org.mgd.jab.objet;

import org.mgd.jab.dto.JeuDto;
import org.mgd.jab.persistence.JeuJao;
import org.mgd.jab.persistence.JeuType;
import org.mgd.jab.persistence.PegiJao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Jeu extends Jo<JeuDto> {
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
    public JeuDto dto() {
        return new JeuJao().decharger(this);
    }

    @Override
    public void depuis(JeuDto dto) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonVide(dto.getNom(), "Le nom du jeu est obligatoire");
        Verifications.nonNull(dto.getType(), "Le type d''un jeu devrait être une des valeurs {0}", Arrays.stream(JeuType.values()).map(Enum::name).collect(Collectors.joining(", ")));
        Verifications.nonAnnee(dto.getAnnee(), "L''année du jeu est en dehors de la plage acceptée");
        Verifications.nonSemaine(dto.getSemaine(), dto.getAnnee(), "Le numéro de la semaine annuel du jeu est en dehors de la plage acceptée");

        setType(dto.getType());
        setNom(dto.getNom());
        setAnnee(dto.getAnnee());
        setSemaine(dto.getSemaine());

        if (!Objects.isNull(dto.getPegi())) {
            setPegi(new PegiJao().charger(dto.getPegi(), this));
        }
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Jeu jeu)) return false;
        return type.idem(jeu.type) && nom.idem(jeu.nom) && annee.idem(jeu.annee) && semaine.idem(jeu.semaine) && pegi.idem(jeu.pegi);
    }
}
