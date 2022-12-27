package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.commun.TypeRepas;
import org.mgd.gmel.coeur.dto.FormuleDto;
import org.mgd.gmel.coeur.objet.Formule;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.gmel.coeur.objet.Periode;
import org.mgd.gmel.coeur.objet.Recette;
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
        Recette recette = new Recette();
        recette.setIdentifiant(UUID.fromString("7a2f76bc-5aba-4f3d-b094-7875f9a6ea2a"));
        recette.setNom("Recette");
        recette.setNombrePersonnes(1);

        Periode periode = new Periode();
        periode.setIdentifiant(UUID.fromString("46fa0eb8-8e68-4760-8215-8b0dcec306c9"));
        periode.setRepas(LocalRepas.pour(2000, 1, 1, TypeRepas.DEJEUNER));
        periode.setTaille(5);

        LivreCuisine livreCuisine = new LivreCuisine();
        livreCuisine.setIdentifiant(UUID.fromString("961b0d52-8ca0-4dd3-9ac0-4bf7e7c4b29e"));
        livreCuisine.setNom("Livre de cuisine");
        livreCuisine.getRecettes().add(recette);

        Formule formule = new Formule();
        formule.setIdentifiant(UUID.fromString("98a1b523-c599-46cf-b0ab-443f28aeeb86"));
        formule.setRecette(recette);
        formule.setPeriode(periode);
        formule.setNombreConvives(1);

        return formule;
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
