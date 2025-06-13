package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.gmel.coeur.dto.EpicerieDto;
import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.gmel.coeur.source.EpicerieAd;
import org.mgd.gmel.coeur.source.EpicerieAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

class EpicerieJaoTest extends AbstractMetierTest<EpicerieDto, Epicerie, EpicerieAf, EpicerieAd> {
    @Override
    protected Epicerie construire() {
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

        Epicerie epicerie = new Epicerie();
        epicerie.setIdentifiant(UUID.fromString("35a6a780-e6d2-4ef9-b2e2-aba5312110a0"));
        epicerie.getProduits().addAll(List.of(produit1, produit2, produit3, produit4));

        return epicerie;
    }

    @Override
    protected EpicerieAd construireAd(Path ressources) {
        return new EpicerieAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        Epicerie epicerieActuel = adObjet.access("epicerie").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(epicerieActuel)),
                () -> Assertions.assertEquals(attendu, epicerieActuel),
                () -> Assertions.assertNotSame(attendu, epicerieActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        Epicerie epicerieActuel = adSupprimable.epicerie("epicerie");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(epicerieActuel),
                () -> Assertions.assertNotNull(epicerieActuel.getProduits()),
                () -> Assertions.assertTrue(epicerieActuel.getProduits().isEmpty())
        );
    }
}
