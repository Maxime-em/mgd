package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.dto.InventaireDto;
import org.mgd.gmel.coeur.objet.Inventaire;
import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.gmel.coeur.objet.Quantite;
import org.mgd.gmel.coeur.source.InventaireAd;
import org.mgd.gmel.coeur.source.InventaireAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

class InventaireJaoTest extends AbstractMetierTest<InventaireDto, Inventaire, InventaireAf, InventaireAd> {

    @Override
    protected Inventaire construire() {
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

        Inventaire inventaire = new Inventaire();
        inventaire.setIdentifiant(UUID.fromString("247a22cb-1a39-4d6e-b2da-b42d6c9d25be"));
        inventaire.getProduitsQuantifier().add(produitQuantifier);

        return inventaire;
    }

    @Override
    protected InventaireAd construireAd(Path ressources) {
        return new InventaireAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        Inventaire inventaireActuel = adObjet.access("inventaire").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(inventaireActuel)),
                () -> Assertions.assertEquals(attendu, inventaireActuel),
                () -> Assertions.assertNotSame(attendu, inventaireActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        Inventaire inventaireActuel = adSupprimable.inventaire("inventaire");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(inventaireActuel),
                () -> Assertions.assertNotNull(inventaireActuel.getProduitsQuantifier()),
                () -> Assertions.assertTrue(inventaireActuel.getProduitsQuantifier().isEmpty())
        );
    }
}
