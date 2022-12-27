package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.gmel.coeur.dto.RecetteDto;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.gmel.coeur.source.RecetteAd;
import org.mgd.gmel.coeur.source.RecetteAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

class RecetteJaoTest extends AbstractMetierTest<RecetteDto, Recette, RecetteAf, RecetteAd> {
    @Override
    protected Recette construire() {
        Recette recette = new Recette();
        recette.setIdentifiant(UUID.fromString("7a2f76bc-5aba-4f3d-b094-7875f9a6ea2a"));
        recette.setNom("Recette");
        recette.setNombrePersonnes(1);

        return recette;
    }

    @Override
    protected RecetteAd construireAd(Path ressources) {
        return new RecetteAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        Recette recetteActuel = adObjet.access("recette").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(recetteActuel)),
                () -> Assertions.assertEquals(attendu, recetteActuel),
                () -> Assertions.assertNotSame(attendu, recetteActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        Recette recetteActuel = adSupprimable.recette("recette");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(recetteActuel),
                () -> Assertions.assertEquals("Recette", recetteActuel.getNom()),
                () -> Assertions.assertEquals(1, recetteActuel.getNombrePersonnes()),
                () -> Assertions.assertNotNull(recetteActuel.getProduitsQuantifier()),
                () -> Assertions.assertEquals(0, recetteActuel.getProduitsQuantifier().size())
        );
    }
}
