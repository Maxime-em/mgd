package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.ProduitQuantifierDto;
import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;

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
    protected ProduitQuantifierDto to(ProduitQuantifier produitQuantifier) {
        ProduitQuantifierDto produitQuantifierDto = new ProduitQuantifierDto();
        produitQuantifierDto.setProduit(new ProduitJao().dechargerVersReference(produitQuantifier.getProduit(), Epicerie.class, EpicerieJao.class));
        produitQuantifierDto.setQuantite(new QuantiteJao().decharger(produitQuantifier.getQuantite()));

        return produitQuantifierDto;
    }

    @Override
    protected void copier(ProduitQuantifier source, ProduitQuantifier cible) throws JaoExecutionException {
        cible.setProduit(new ProduitJao().dupliquer(source.getProduit()));
        cible.setQuantite(new QuantiteJao().dupliquer(source.getQuantite()));
    }
}
