package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.ProduitQuantifierDto;
import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

/**
 * Classe permettant de manipuler les objets métiers de type {@link ProduitQuantifier} et de les charger depuis un
 * système de fichiers JSON via la classe de transfert {@link ProduitQuantifierDto}.
 *
 * @author Maxime
 */
public class ProduitQuantifierJao extends Jao<ProduitQuantifierDto, ProduitQuantifier> {
    public ProduitQuantifierJao() {
        super(ProduitQuantifierDto.class, ProduitQuantifier.class);
    }

    @Override
    public ProduitQuantifierDto dto(ProduitQuantifier produitQuantifier) {
        ProduitQuantifierDto produitQuantifierDto = new ProduitQuantifierDto();
        produitQuantifierDto.setProduit(new ProduitJao().dechargerVersReference(produitQuantifier.getProduit(), Epicerie.class, EpicerieJao.class));
        produitQuantifierDto.setQuantite(new QuantiteJao().decharger(produitQuantifier.getQuantite()));

        return produitQuantifierDto;
    }

    @Override
    public void enrichir(ProduitQuantifierDto dto, ProduitQuantifier produitQuantifier) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getProduit(), "Un produit quantifié doit contenir un produit");
        Verifications.nonNull(dto.getQuantite(), "Un produit quantifié doit contenir une quantité");

        produitQuantifier.setProduit(new ProduitJao().chargerParReference(dto.getProduit()));
        produitQuantifier.setQuantite(new QuantiteJao().charger(dto.getQuantite(), produitQuantifier));
    }

    @Override
    protected void copier(ProduitQuantifier source, ProduitQuantifier cible) throws JaoExecutionException, JaoParseException {
        cible.setProduit(new ProduitJao().dupliquer(source.getProduit()));
        cible.setQuantite(new QuantiteJao().dupliquer(source.getQuantite()));
    }
}
