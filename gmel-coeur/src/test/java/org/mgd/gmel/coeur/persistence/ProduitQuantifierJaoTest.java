package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.dto.ProduitQuantifierDto;
import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.gmel.coeur.objet.Quantite;
import org.mgd.gmel.coeur.source.ProduitQuantifierAd;
import org.mgd.gmel.coeur.source.ProduitQuantifierAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

class ProduitQuantifierJaoTest extends AbstractMetierTest<ProduitQuantifierDto, ProduitQuantifier, ProduitQuantifierAf, ProduitQuantifierAd> {
    @Override
    protected ProduitQuantifier construire() {
        Produit produit = new Produit();
        produit.setIdentifiant(UUID.fromString("549c3eea-d12d-4428-aa32-dc600f7381ab"));
        produit.setNom("Produit");

        Quantite quantite = new Quantite();
        quantite.setIdentifiant(UUID.fromString("66a118e3-27f6-487e-8b0f-f345821cb149"));
        quantite.setMesure(Mesure.MASSE);
        quantite.setValeur(200L);

        ProduitQuantifier produitQuantifier = new ProduitQuantifier();
        produitQuantifier.setIdentifiant(UUID.fromString("d4a31317-e70a-4cdb-9527-78a5ae10c4a0"));
        produitQuantifier.setProduit(produit);
        produitQuantifier.setQuantite(quantite);

        return produitQuantifier;
    }

    @Override
    protected ProduitQuantifierAd construireAd(Path ressources) {
        return new ProduitQuantifierAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        ProduitQuantifier produitQuantifierActuel = adObjet.access("produit_quantifier").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(produitQuantifierActuel)),
                () -> Assertions.assertEquals(attendu, produitQuantifierActuel),
                () -> Assertions.assertNotSame(attendu, produitQuantifierActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        ProduitQuantifier produitQuantifierActuel = adSupprimable.produitQuantifier("produit_quantifier");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(produitQuantifierActuel),
                () -> Assertions.assertNotNull(produitQuantifierActuel.getProduit()),
                () -> Assertions.assertEquals("produit", produitQuantifierActuel.getProduit().getNom()),
                () -> Assertions.assertNotNull(produitQuantifierActuel.getQuantite()),
                () -> Assertions.assertEquals(1, produitQuantifierActuel.getQuantite().getValeur()),
                () -> Assertions.assertEquals(Mesure.MASSE, produitQuantifierActuel.getQuantite().getMesure())
        );
    }
}
