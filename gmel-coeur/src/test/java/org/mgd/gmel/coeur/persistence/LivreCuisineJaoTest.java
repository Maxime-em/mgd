package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.gmel.coeur.dto.LivreCuisineDto;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.gmel.coeur.source.LivreCuisineAd;
import org.mgd.gmel.coeur.source.LivreCuisineAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

class LivreCuisineJaoTest extends AbstractMetierTest<LivreCuisineDto, LivreCuisine, LivreCuisineAf, LivreCuisineAd> {
    @Override
    protected LivreCuisine construire() {
        Recette recette1 = new Recette();
        recette1.setIdentifiant(UUID.fromString("f1a7f4c8-8b8b-456d-956d-9ba580f64be6"));
        recette1.setNom("Recette 1");
        recette1.setNombrePersonnes(1);

        Recette recette2 = new Recette();
        recette2.setIdentifiant(UUID.fromString("c9e8b622-3b84-442e-956a-34ebc2638ac3"));
        recette2.setNom("Recette 2");
        recette2.setNombrePersonnes(2);

        Recette recette3 = new Recette();
        recette3.setIdentifiant(UUID.fromString("19446c18-6286-452b-b2e3-278ca941b1e3"));
        recette3.setNom("Recette 3");
        recette3.setNombrePersonnes(3);

        LivreCuisine livreCuisine = new LivreCuisine();
        livreCuisine.setIdentifiant(UUID.fromString("7cb21251-d5db-41ce-ae84-1934e63411a1"));
        livreCuisine.setNom("Livre de cuisine 1");
        livreCuisine.getRecettes().addAll(List.of(recette1, recette2, recette3));

        return livreCuisine;
    }

    @Override
    protected LivreCuisineAd construireAd(Path ressources) {
        return new LivreCuisineAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        LivreCuisine livreCuisineActuel = adObjet.access("livre_cuisine").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(livreCuisineActuel)),
                () -> Assertions.assertEquals(attendu, livreCuisineActuel),
                () -> Assertions.assertNotSame(attendu, livreCuisineActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        LivreCuisine livreCuisineActuel = adSupprimable.livreCuisine("livre_cuisine");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(livreCuisineActuel),
                () -> Assertions.assertNotNull(livreCuisineActuel.getRecettes()),
                () -> Assertions.assertTrue(livreCuisineActuel.getRecettes().isEmpty()),
                () -> Assertions.assertEquals("Livre de cuisine", livreCuisineActuel.getNom())
        );
    }
}
