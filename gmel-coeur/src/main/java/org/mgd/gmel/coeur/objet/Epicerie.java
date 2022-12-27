package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.dto.EpicerieDto;
import org.mgd.gmel.coeur.persistence.EpicerieJao;
import org.mgd.gmel.coeur.persistence.ProduitJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.JocTreeSet;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.SortedSet;

/**
 * Objet métier représentant une épicerie qui met à disposition des produits.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Epicerie extends Jo<EpicerieDto> {
    private final SortedSet<Produit> produits = new JocTreeSet<>(this);

    public SortedSet<Produit> getProduits() {
        return produits;
    }

    @Override
    public EpicerieDto dto() {
        return new EpicerieJao().decharger(this);
    }

    @Override
    public void depuis(EpicerieDto dto) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getProduits(), "Les produits d''une épicerie devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getProduits(),
                (produitDto1, produitDto2) -> produitDto1.getNom().equals(produitDto2.getNom()),
                "Les noms des produits dans une épicerie doivent être unique"
        );
        getProduits().addAll(new ProduitJao().charger(dto.getProduits(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Epicerie epicerie)) return false;
        return ((JocTreeSet<Produit>) produits).idem(epicerie.produits);
    }
}
