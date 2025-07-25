package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.ProduitDto;
import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

/**
 * Classe permettant de manipuler les objets métiers de type {@link Produit} et de les charger depuis un système de
 * fichiers JSON via la classe de transfert {@link ProduitDto}.
 *
 * @author Maxime
 */
public class ProduitJao extends Jao<ProduitDto, Produit> {
    public ProduitJao() {
        super(ProduitDto.class, Produit.class);
    }

    @Override
    public ProduitDto dto(Produit produit) {
        ProduitDto produitDto = new ProduitDto();
        produitDto.setNom(produit.getNom());

        return produitDto;
    }

    @Override
    public void enrichir(ProduitDto dto, Produit produit) throws VerificationException {
        Verifications.nonVide(dto.getNom(), "Le nom de produit \"{0}\" est incorrect");

        produit.setNom(dto.getNom());
    }

    @Override
    protected void copier(Produit source, Produit cible) {
        cible.setNom(source.getNom());
    }
}
