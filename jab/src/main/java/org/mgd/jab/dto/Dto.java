package org.mgd.jab.dto;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.Jao;

/**
 * Classe de base des objets utilisés par {@link Jao} pour transformer les données
 * provenant d'un système de fichiers JSON vers un objet {@link Jo}.
 * Tous ces objets doivent au moins avoir un identifiant unique.
 *
 * @author Maxime
 */
public abstract class Dto {
    private String identifiant;

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }
}
