package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.Bibliotheque;
import org.mgd.jab.dto.Dto;

import java.util.List;

/**
 * Classe de transformation vers l'objet métier {@link Bibliotheque} provenant d'un système de
 * fichiers JSON.
 *
 * @author Maxime
 */
public class BibliothequeDto extends Dto {
    private List<LivreCuisineDto> livresCuisine;

    public List<LivreCuisineDto> getLivresCuisine() {
        return livresCuisine;
    }

    public void setLivresCuisine(List<LivreCuisineDto> livresCuisine) {
        this.livresCuisine = livresCuisine;
    }
}
