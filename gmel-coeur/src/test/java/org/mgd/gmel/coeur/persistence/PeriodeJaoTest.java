package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.commun.TypeRepas;
import org.mgd.gmel.coeur.dto.PeriodeDto;
import org.mgd.gmel.coeur.objet.Periode;
import org.mgd.gmel.coeur.source.PeriodeAd;
import org.mgd.gmel.coeur.source.PeriodeAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.temps.LocalRepas;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

class PeriodeJaoTest extends AbstractMetierTest<PeriodeDto, Periode, PeriodeAf, PeriodeAd> {
    @Override
    protected Periode construire() {
        Periode periode = new Periode();
        periode.setIdentifiant(UUID.fromString("46fa0eb8-8e68-4760-8215-8b0dcec306c9"));
        periode.setRepas(LocalRepas.pour(2000, 1, 1, TypeRepas.DEJEUNER));
        periode.setTaille(5);

        return periode;
    }

    @Override
    protected PeriodeAd construireAd(Path ressources) {
        return new PeriodeAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        Periode periodeActuel = adObjet.access("periode").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(periodeActuel)),
                () -> Assertions.assertEquals(attendu, periodeActuel),
                () -> Assertions.assertNotSame(attendu, periodeActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        Periode periodeActuel = adSupprimable.periode("periode");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(periodeActuel),
                () -> Assertions.assertEquals(LocalRepas.pour(LocalDate.now(), TypeRepas.PETIT_DEJEUNER), periodeActuel.getRepas()),
                () -> Assertions.assertEquals(1, periodeActuel.getTaille())
        );
    }
}
