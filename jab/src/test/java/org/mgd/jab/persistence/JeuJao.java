package org.mgd.jab.persistence;

import org.mgd.jab.dto.JeuDto;
import org.mgd.jab.objet.Jeu;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class JeuJao extends Jao<JeuDto, Jeu> {
    public JeuJao() {
        super(JeuDto.class, Jeu.class);
    }

    @Override
    public JeuDto dto(Jeu jeu) {
        JeuDto dto = new JeuDto();
        dto.setType(jeu.getType());
        dto.setNom(jeu.getNom());
        dto.setAnnee(jeu.getAnnee());
        dto.setSemaine(jeu.getSemaine());
        dto.setPegi(new PegiJao().decharger(jeu.getPegi()));
        return dto;
    }

    @Override
    public void enrichir(JeuDto dto, Jeu jeu) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonVide(dto.getNom(), "Le nom du jeu est obligatoire");
        Verifications.nonNull(dto.getType(), "Le type d''un jeu devrait être une des valeurs {0}", Arrays.stream(JeuType.values()).map(Enum::name).collect(Collectors.joining(", ")));
        Verifications.nonAnnee(dto.getAnnee(), "L''année du jeu est en dehors de la plage acceptée");
        Verifications.nonSemaine(dto.getSemaine(), dto.getAnnee(), "Le numéro de la semaine annuel du jeu est en dehors de la plage acceptée");

        jeu.setType(dto.getType());
        jeu.setNom(dto.getNom());
        jeu.setAnnee(dto.getAnnee());
        jeu.setSemaine(dto.getSemaine());

        if (!Objects.isNull(dto.getPegi())) {
            jeu.setPegi(new PegiJao().charger(dto.getPegi(), jeu));
        }
    }

    @Override
    protected void copier(Jeu source, Jeu cible) throws JaoExecutionException, JaoParseException {
        cible.setType(source.getType());
        cible.setNom(source.getNom());
        cible.setAnnee(source.getAnnee());
        cible.setSemaine(source.getSemaine());

        if (!Objects.isNull(source.getPegi())) {
            cible.setPegi(new PegiJao().dupliquer(source.getPegi()));
        }
    }
}
