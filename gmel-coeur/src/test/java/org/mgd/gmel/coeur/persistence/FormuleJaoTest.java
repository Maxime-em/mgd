package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.commun.TypeRepas;
import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.dto.FormuleDto;
import org.mgd.gmel.coeur.objet.*;
import org.mgd.gmel.coeur.source.FormuleAd;
import org.mgd.gmel.coeur.source.FormuleAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.temps.LocalRepas;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

class FormuleJaoTest extends AbstractMetierTest<FormuleDto, Formule, FormuleAf, FormuleAd> {
    @Override
    protected Formule construire() {
        Produit produit1 = new Produit();
        produit1.setIdentifiant(UUID.fromString("eee19e2d-2796-4082-b8cc-218d3a14e955"));
        produit1.setNom("Produit 1");

        Produit produit2 = new Produit();
        produit2.setIdentifiant(UUID.fromString("7fc2e02a-fca9-4693-a2b7-9d8464f38bb7"));
        produit2.setNom("Produit 2");

        Quantite quantite11 = new Quantite();
        quantite11.setIdentifiant(UUID.fromString("6bf679b5-1188-4ac2-99c5-a11aa36dfc41"));
        quantite11.setValeur(10L);
        quantite11.setMesure(Mesure.VOLUME);

        ProduitQuantifier produitQuantifier11 = new ProduitQuantifier();
        produitQuantifier11.setIdentifiant(UUID.fromString("1f4011e5-e7c9-433f-bf3a-7111975782f8"));
        produitQuantifier11.setProduit(produit1);
        produitQuantifier11.setQuantite(quantite11);

        Quantite quantite12 = new Quantite();
        quantite12.setIdentifiant(UUID.fromString("794d971f-2e23-4657-8b67-8c8a09800d5b"));
        quantite12.setValeur(20L);
        quantite12.setMesure(Mesure.MASSE);

        ProduitQuantifier produitQuantifier12 = new ProduitQuantifier();
        produitQuantifier12.setIdentifiant(UUID.fromString("16c89cf4-725f-483d-a98f-e9ca50d470ae"));
        produitQuantifier12.setProduit(produit2);
        produitQuantifier12.setQuantite(quantite12);

        Recette recette1 = new Recette();
        recette1.setIdentifiant(UUID.fromString("f1a7f4c8-8b8b-456d-956d-9ba580f64be6"));
        recette1.setNom("Recette 1");
        recette1.setNombrePersonnes(1);
        recette1.getProduitsQuantifier().add(produitQuantifier11);
        recette1.getProduitsQuantifier().add(produitQuantifier12);

        Periode periode1 = new Periode();
        periode1.setIdentifiant(UUID.fromString("46fa0eb8-8e68-4760-8215-8b0dcec306c9"));
        periode1.setRepas(LocalRepas.pour(2000, 1, 1, TypeRepas.DEJEUNER));
        periode1.setTaille(5);

        Formule formule1 = new Formule();
        formule1.setIdentifiant(UUID.fromString("98a1b523-c599-46cf-b0ab-443f28aeeb86"));
        formule1.setRecette(recette1);
        formule1.setPeriode(periode1);
        formule1.setNombreConvives(1);

        return formule1;
    }

    @Override
    protected FormuleAd construireAd(Path ressources) {
        return new FormuleAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        Formule formuleActuel = adObjet.access("formule").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(formuleActuel)),
                () -> Assertions.assertEquals(attendu, formuleActuel),
                () -> Assertions.assertNotSame(attendu, formuleActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        Formule formuleActuel = adSupprimable.access("formule").jo();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(formuleActuel),
                () -> Assertions.assertNull(formuleActuel.getRecette()),
                () -> Assertions.assertNull(formuleActuel.getPeriode()),
                () -> Assertions.assertNull(formuleActuel.getNombreConvives())
        );
    }
}
