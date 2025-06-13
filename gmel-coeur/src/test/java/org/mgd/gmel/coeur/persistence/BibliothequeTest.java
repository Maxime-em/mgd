package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.dto.BibliothequeDto;
import org.mgd.gmel.coeur.objet.*;
import org.mgd.gmel.coeur.source.BibliothequeAd;
import org.mgd.gmel.coeur.source.BibliothequeAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

class BibliothequeTest extends AbstractMetierTest<BibliothequeDto, Bibliotheque, BibliothequeAf, BibliothequeAd> {
    @Override
    protected Bibliotheque construire() {
        Produit produit1 = new Produit();
        produit1.setIdentifiant(UUID.fromString("eee19e2d-2796-4082-b8cc-218d3a14e955"));
        produit1.setNom("Produit 1");

        Produit produit2 = new Produit();
        produit2.setIdentifiant(UUID.fromString("7fc2e02a-fca9-4693-a2b7-9d8464f38bb7"));
        produit2.setNom("Produit 2");

        Produit produit3 = new Produit();
        produit3.setIdentifiant(UUID.fromString("6da29b2f-a8e4-42ba-ab26-2df048fa67df"));
        produit3.setNom("Produit 3");

        Produit produit4 = new Produit();
        produit4.setIdentifiant(UUID.fromString("56b4ee41-d325-4ea1-85d8-4b3acd8f976a"));
        produit4.setNom("Produit 4");

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

        Quantite quantite21 = new Quantite();
        quantite21.setIdentifiant(UUID.fromString("dc7f7747-a0bf-4a29-a3c5-564b101c9e01"));
        quantite21.setValeur(100L);
        quantite21.setMesure(Mesure.VOLUME);

        ProduitQuantifier produitQuantifier21 = new ProduitQuantifier();
        produitQuantifier21.setIdentifiant(UUID.fromString("3eb0787e-6736-42e0-adb9-c8d0ee55b3b8"));
        produitQuantifier21.setProduit(produit1);
        produitQuantifier21.setQuantite(quantite21);

        Quantite quantite22 = new Quantite();
        quantite22.setIdentifiant(UUID.fromString("087e379f-afb4-40ea-b92e-89b1a4a99312"));
        quantite22.setValeur(200L);
        quantite22.setMesure(Mesure.VOLUME);

        ProduitQuantifier produitQuantifier22 = new ProduitQuantifier();
        produitQuantifier22.setIdentifiant(UUID.fromString("63a4e729-3112-4695-8a14-eaec68c87513"));
        produitQuantifier22.setProduit(produit2);
        produitQuantifier22.setQuantite(quantite22);

        Quantite quantite23 = new Quantite();
        quantite23.setIdentifiant(UUID.fromString("5c51517d-c5fd-4126-a036-8fdc24f7eab4"));
        quantite23.setValeur(300L);
        quantite23.setMesure(Mesure.VOLUME);

        ProduitQuantifier produitQuantifier23 = new ProduitQuantifier();
        produitQuantifier23.setIdentifiant(UUID.fromString("4b910191-462f-4de1-9541-afc5ddfc122f"));
        produitQuantifier23.setProduit(produit3);
        produitQuantifier23.setQuantite(quantite23);

        Recette recette2 = new Recette();
        recette2.setIdentifiant(UUID.fromString("c9e8b622-3b84-442e-956a-34ebc2638ac3"));
        recette2.setNom("Recette 2");
        recette2.setNombrePersonnes(2);
        recette2.getProduitsQuantifier().add(produitQuantifier21);
        recette2.getProduitsQuantifier().add(produitQuantifier22);
        recette2.getProduitsQuantifier().add(produitQuantifier23);

        Quantite quantite31 = new Quantite();
        quantite31.setIdentifiant(UUID.fromString("f49ee008-74ad-4ccc-ab50-ca822637cd36"));
        quantite31.setValeur(1000L);
        quantite31.setMesure(Mesure.MASSE);

        ProduitQuantifier produitQuantifier31 = new ProduitQuantifier();
        produitQuantifier31.setIdentifiant(UUID.fromString("a5d87745-d0b8-4387-bfee-bf6d97af0dba"));
        produitQuantifier31.setProduit(produit4);
        produitQuantifier31.setQuantite(quantite31);

        Recette recette3 = new Recette();
        recette3.setIdentifiant(UUID.fromString("19446c18-6286-452b-b2e3-278ca941b1e3"));
        recette3.setNom("Recette 3");
        recette3.setNombrePersonnes(3);
        recette3.getProduitsQuantifier().add(produitQuantifier31);

        LivreCuisine livreCuisine = new LivreCuisine();
        livreCuisine.setIdentifiant(UUID.fromString("7cb21251-d5db-41ce-ae84-1934e63411a1"));
        livreCuisine.setNom("Livre de cuisine 1");
        livreCuisine.getRecettes().addAll(List.of(recette1, recette2, recette3));

        Bibliotheque bibliotheque = new Bibliotheque();
        bibliotheque.setIdentifiant(UUID.fromString("3202c9e1-ae19-4a8c-aab2-aeae0e9f6e77"));
        bibliotheque.getLivresCuisine().add(livreCuisine);

        return bibliotheque;
    }

    @Override
    protected BibliothequeAd construireAd(Path ressources) {
        return new BibliothequeAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        Bibliotheque bibliothequeActuel = adObjet.access("bibliotheque").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(bibliothequeActuel)),
                () -> Assertions.assertEquals(attendu, bibliothequeActuel),
                () -> Assertions.assertNotSame(attendu, bibliothequeActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        Bibliotheque bibliothequeActuel = adSupprimable.bibliotheque("bibliotheque");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(bibliothequeActuel),
                () -> Assertions.assertNotNull(bibliothequeActuel.getLivresCuisine()),
                () -> Assertions.assertTrue(bibliothequeActuel.getLivresCuisine().isEmpty())
        );
    }
}
