package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.commun.TypeRepas;
import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.dto.AgendaDto;
import org.mgd.gmel.coeur.objet.*;
import org.mgd.gmel.coeur.source.AgendaAd;
import org.mgd.gmel.coeur.source.AgendaAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.temps.LocalRepas;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

class AgendaJaoTest extends AbstractMetierTest<AgendaDto, Agenda, AgendaAf, AgendaAd> {
    @Override
    protected Agenda construire() {
        Produit produit1 = new Produit();
        produit1.setIdentifiant(UUID.fromString("eee19e2d-2796-4082-b8cc-218d3a14e955"));
        produit1.setNom("Produit 1");

        Produit produit2 = new Produit();
        produit2.setIdentifiant(UUID.fromString("7fc2e02a-fca9-4693-a2b7-9d8464f38bb7"));
        produit2.setNom("Produit 2");

        Produit produit3 = new Produit();
        produit3.setIdentifiant(UUID.fromString("6da29b2f-a8e4-42ba-ab26-2df048fa67df"));
        produit3.setNom("Produit 3");

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

        Periode periode1 = new Periode();
        periode1.setIdentifiant(UUID.fromString("46fa0eb8-8e68-4760-8215-8b0dcec306c9"));
        periode1.setRepas(LocalRepas.pour(2000, 1, 3, TypeRepas.DEJEUNER));
        periode1.setTaille(5);

        Formule formule1 = new Formule();
        formule1.setIdentifiant(UUID.fromString("98a1b523-c599-46cf-b0ab-443f28aeeb86"));
        formule1.setRecette(recette1);
        formule1.setPeriode(periode1);
        formule1.setNombreConvives(1);

        Periode periode2 = new Periode();
        periode2.setIdentifiant(UUID.fromString("74a59cb3-9f92-415e-b88c-5fe47ad57e20"));
        periode2.setRepas(LocalRepas.pour(2000, 1, 5, TypeRepas.DEJEUNER));
        periode2.setTaille(2);

        Formule formule2 = new Formule();
        formule2.setIdentifiant(UUID.fromString("98a1b523-c599-46cf-b0ab-443f28aeeb86"));
        formule2.setRecette(recette2);
        formule2.setPeriode(periode2);
        formule2.setNombreConvives(2);

        Menu menu = new Menu();
        menu.setIdentifiant(UUID.fromString("0134cc77-8cda-4541-9525-355d66c93f51"));
        menu.setAnnee(2000);
        menu.setSemaine(1);
        menu.getFormules().addAll(List.of(formule1, formule2));

        Agenda agenda = new Agenda();
        agenda.setIdentifiant(UUID.fromString("a16f5359-641d-4aa4-8da9-073526f4a290"));
        agenda.getMenus().add(menu);

        return agenda;
    }

    @Override
    protected AgendaAd construireAd(Path ressources) {
        return new AgendaAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        Agenda agendaActuel = adObjet.access("agenda").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(agendaActuel)),
                () -> Assertions.assertEquals(attendu, agendaActuel),
                () -> Assertions.assertNotSame(attendu, agendaActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        Agenda agendaActuel = adSupprimable.agenda("agenda");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(agendaActuel),
                () -> Assertions.assertNotNull(agendaActuel.getMenus()),
                () -> Assertions.assertTrue(agendaActuel.getMenus().isEmpty())
        );
    }
}
