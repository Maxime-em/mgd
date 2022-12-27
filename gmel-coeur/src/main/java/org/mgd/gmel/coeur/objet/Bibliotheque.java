package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.dto.BibliothequeDto;
import org.mgd.gmel.coeur.persistence.BibliothequeJao;
import org.mgd.gmel.coeur.persistence.LivreCuisineJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.JocTreeSet;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.SortedSet;

/**
 * Objet métier représentant une bibliothèque de livres de cuisine.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Bibliotheque extends Jo<BibliothequeDto> {
    private final SortedSet<LivreCuisine> livresCuisine = new JocTreeSet<>(this);

    public SortedSet<LivreCuisine> getLivresCuisine() {
        return livresCuisine;
    }

    @Override
    public BibliothequeDto dto() {
        return new BibliothequeJao().decharger(this);
    }

    @Override
    public void depuis(BibliothequeDto dto) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getLivresCuisine(), "Les livres de cuisine d''une bibliothèques devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getLivresCuisine(),
                (livreCuisineDto1, livreCuisineDto2) -> livreCuisineDto1.getNom().equals(livreCuisineDto2.getNom()),
                "Les noms des livres de cuisines dans un bibliothèque doivent être unique"
        );
        getLivresCuisine().addAll(new LivreCuisineJao().charger(dto.getLivresCuisine(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Bibliotheque bibliotheque)) return false;
        return ((JocTreeSet<LivreCuisine>) livresCuisine).idem(bibliotheque.livresCuisine);
    }
}
