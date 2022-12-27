package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.dto.InventaireDto;
import org.mgd.gmel.coeur.persistence.InventaireJao;
import org.mgd.gmel.coeur.persistence.ProduitQuantifierJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.JocTreeSet;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.SortedSet;

/**
 * Objet métier représentant un inventaire de produits quantifiés.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Inventaire extends Jo<InventaireDto> {
    private final SortedSet<ProduitQuantifier> produitsQuantifier = new JocTreeSet<>(this);

    public SortedSet<ProduitQuantifier> getProduitsQuantifier() {
        return produitsQuantifier;
    }

    @Override
    public InventaireDto dto() {
        return new InventaireJao().decharger(this);
    }

    @Override
    public void depuis(InventaireDto dto) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getProduitsQuantifier(), "Les produits quantifiés d''une liste de courses devrait être une liste éventuellement vide");

        getProduitsQuantifier().addAll(new ProduitQuantifierJao().charger(dto.getProduitsQuantifier(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Inventaire inventaire)) return false;
        return ((JocTreeSet<ProduitQuantifier>) produitsQuantifier).idem(inventaire.produitsQuantifier);
    }
}
