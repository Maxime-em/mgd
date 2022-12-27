package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.gmel.coeur.dto.ProduitDto;
import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.gmel.coeur.source.ProduitAd;
import org.mgd.gmel.coeur.source.ProduitAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

class ProduitJaoTest extends AbstractMetierTest<ProduitDto, Produit, ProduitAf, ProduitAd> {
    @Override
    protected Produit construire() {
        Produit produit = new Produit();
        produit.setIdentifiant(UUID.fromString("549c3eea-d12d-4428-aa32-dc600f7381ab"));
        produit.setNom("Produit");

        return produit;
    }

    @Override
    protected ProduitAd construireAd(Path ressources) {
        return new ProduitAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        Produit produitActuel = adObjet.access("produit").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(produitActuel)),
                () -> Assertions.assertEquals(attendu, produitActuel),
                () -> Assertions.assertNotSame(attendu, produitActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        Produit produitActuel = adSupprimable.produit("produit");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(produitActuel),
                () -> Assertions.assertEquals("Produit", produitActuel.getNom())
        );
    }
}
