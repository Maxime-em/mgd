package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.objet.Quantite;
import org.mgd.jab.dto.Dto;

/**
 * Classe de transformation vers l'objet métier {@link Quantite} provenant d'un système de
 * fichiers JSON.
 *
 * @author Maxime
 */
public class QuantiteDto extends Dto {
    private Long valeur;
    private Mesure mesure;

    public Long getValeur() {
        return valeur;
    }

    public void setValeur(Long valeur) {
        this.valeur = valeur;
    }

    public Mesure getMesure() {
        return mesure;
    }

    public void setMesure(Mesure mesure) {
        this.mesure = mesure;
    }
}
