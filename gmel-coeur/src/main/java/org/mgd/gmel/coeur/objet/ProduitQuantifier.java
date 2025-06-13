package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.dto.ProduitQuantifierDto;
import org.mgd.gmel.coeur.persistence.ProduitJao;
import org.mgd.gmel.coeur.persistence.ProduitQuantifierJao;
import org.mgd.gmel.coeur.persistence.QuantiteJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.Comparator;

/**
 * Objet métier représentant un produit associé à une quantité qui feront partie d'une recette de cuisines.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class ProduitQuantifier extends Jo<ProduitQuantifierDto> implements Comparable<ProduitQuantifier> {
    private final Joc<Produit> produit = new Joc<>(this);
    private final Joc<Quantite> quantite = new Joc<>(this);

    public Produit getProduit() {
        return produit.get();
    }

    public void setProduit(Produit produit) {
        this.produit.set(produit);
    }

    public Quantite getQuantite() {
        return quantite.get();
    }

    public void setQuantite(Quantite quantite) {
        this.quantite.set(quantite);
    }

    @Override
    public ProduitQuantifierDto dto() {
        return new ProduitQuantifierJao().decharger(this);
    }

    @Override
    public void depuis(ProduitQuantifierDto dto) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getProduit(), "Un produit quantifié doit contenir un produit");
        Verifications.nonNull(dto.getQuantite(), "Un produit quantifié doit contenir une quantité");

        setProduit(new ProduitJao().chargerParReference(dto.getProduit()));
        setQuantite(new QuantiteJao().charger(dto.getQuantite(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof ProduitQuantifier produitQuantifier)) return false;
        return produit.idem(produitQuantifier.produit) && quantite.idem(produitQuantifier.quantite);
    }

    @Override
    public int compareTo(ProduitQuantifier produitQuantifier) {
        return Comparator.comparing(ProduitQuantifier::getProduit).thenComparing(ProduitQuantifier::getQuantite).compare(this, produitQuantifier);
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
