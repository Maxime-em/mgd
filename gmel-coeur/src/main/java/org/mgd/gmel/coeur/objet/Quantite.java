package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.dto.QuantiteDto;
import org.mgd.gmel.coeur.persistence.QuantiteJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Objet métier représentant une quantité qui sera associée à un produit pour faire partie d'une recette de cuisines.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Quantite extends Jo<QuantiteDto> implements Comparable<Quantite> {
    private final Joc<Long> valeur = new Joc<>(this);
    private final Joc<Mesure> mesure = new Joc<>(this);

    public Long getValeur() {
        return valeur.get();
    }

    public void setValeur(Long valeur) {
        this.valeur.set(valeur);
    }

    public Mesure getMesure() {
        return mesure.get();
    }

    public void setMesure(Mesure mesure) {
        this.mesure.set(mesure);
    }

    @Override
    public QuantiteDto dto() {
        return new QuantiteJao().decharger(this);
    }

    @Override
    public void depuis(QuantiteDto dto) throws VerificationException {
        Verifications.nonNull(dto.getMesure(), "La mesure d''une quantité devrait être une des valeurs {0}", Arrays.stream(Mesure.values()).map(Enum::name).collect(Collectors.joining(", ")));
        Verifications.nonStrictementNegatif(dto.getValeur(), "La valeur de la quantité doit être un entier positif");

        setValeur(dto.getValeur());
        setMesure(dto.getMesure());
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Quantite quantite)) return false;
        return valeur.idem(quantite.valeur) && mesure.idem(quantite.mesure);
    }

    public String getUnite() {
        return mesure.get().getUnite(Math.abs(valeur.get()) < 2);
    }

    @Override
    public int compareTo(Quantite quantite) {
        return Comparator.comparing(Quantite::getMesure).thenComparing(Quantite::getValeur).compare(this, quantite);
    }

    @Override
    public boolean equals(Object objet) {
        return super.equals(objet);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
