package org.mgd.jab.persistence;

import org.mgd.jab.dto.JeuDto;
import org.mgd.jab.objet.Jeu;
import org.mgd.jab.persistence.exception.JaoExecutionException;

import java.util.Objects;

public class JeuJao extends Jao<JeuDto, Jeu> {
    public JeuJao() {
        super(JeuDto.class, Jeu.class);
    }

    @Override
    protected JeuDto to(Jeu jeu) {
        JeuDto dto = new JeuDto();
        dto.setType(jeu.getType());
        dto.setNom(jeu.getNom());
        dto.setAnnee(jeu.getAnnee());
        dto.setSemaine(jeu.getSemaine());
        dto.setPegi(new PegiJao().decharger(jeu.getPegi()));
        return dto;
    }

    @Override
    protected void copier(Jeu source, Jeu cible) throws JaoExecutionException {
        cible.setType(source.getType());
        cible.setNom(source.getNom());
        cible.setAnnee(source.getAnnee());
        cible.setSemaine(source.getSemaine());

        if (!Objects.isNull(source.getPegi())) {
            cible.setPegi(new PegiJao().dupliquer(source.getPegi()));
        }
    }
}
