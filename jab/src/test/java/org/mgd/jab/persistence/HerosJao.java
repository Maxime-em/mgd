package org.mgd.jab.persistence;

import org.mgd.jab.dto.HerosDto;
import org.mgd.jab.objet.Heros;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.ArrayList;

/**
 * Accès aux données des super-héros, objet de test.
 */
public class HerosJao extends Jao<HerosDto, Heros> {
    public HerosJao() {
        super(HerosDto.class, Heros.class);
    }

    @Override
    public HerosDto dto(Heros objet) {
        HerosDto dto = new HerosDto();
        dto.nom = objet.getNom();
        dto.alias = objet.getAlias();
        dto.ville = objet.getVille();
        dto.premiereApparition = objet.getPremiereApparition();
        dto.pouvoirs = new ArrayList<>(objet.getPouvoirs());
        dto.mentor = decharger(objet.getMentor().get());
        dto.allies = decharger(objet.getAllies());
        return dto;
    }

    @Override
    public void enrichir(HerosDto dto, Heros objet) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonVide(dto.nom, "Le nom du héros ne doit pas être vide.");
        objet.setNom(dto.nom);
        objet.setAlias(dto.alias);
        objet.setVille(dto.ville);
        objet.setPremiereApparition(dto.premiereApparition);
        objet.setPouvoirs(dto.pouvoirs == null ? new ArrayList<>() : dto.pouvoirs);
        if (dto.mentor != null) {
            objet.getMentor().set(charger(dto.mentor, objet));
        }
        if (dto.allies != null) {
            objet.getAllies().addAll(charger(dto.allies, objet));
        }
    }

    @Override
    protected void copier(Heros source, Heros cible) {
        cible.setNom(source.getNom());
        cible.setAlias(source.getAlias());
        cible.setVille(source.getVille());
        cible.setPremiereApparition(source.getPremiereApparition());
        cible.setPouvoirs(source.getPouvoirs());
    }
}
