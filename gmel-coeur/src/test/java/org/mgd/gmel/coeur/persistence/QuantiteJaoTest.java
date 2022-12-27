package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.dto.QuantiteDto;
import org.mgd.gmel.coeur.objet.Quantite;
import org.mgd.gmel.coeur.source.QuantiteAd;
import org.mgd.gmel.coeur.source.QuantiteAf;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

class QuantiteJaoTest extends AbstractMetierTest<QuantiteDto, Quantite, QuantiteAf, QuantiteAd> {
    @Override
    protected Quantite construire() {
        Quantite quantite = new Quantite();
        quantite.setIdentifiant(UUID.fromString("66a118e3-27f6-487e-8b0f-f345821cb149"));
        quantite.setValeur(200L);
        quantite.setMesure(Mesure.MASSE);

        return quantite;
    }

    @Override
    protected QuantiteAd construireAd(Path ressources) {
        return new QuantiteAd(ressources);
    }

    @Test
    void depuisFichierExistant() throws IOException, JaoExecutionException, JaoParseException {
        Quantite quantiteActuel = adObjet.access("quantite").jo();
        Assertions.assertAll(
                () -> Assertions.assertTrue(attendu.idem(quantiteActuel)),
                () -> Assertions.assertEquals(attendu, quantiteActuel),
                () -> Assertions.assertNotSame(attendu, quantiteActuel)
        );
    }

    @Test
    void depuisFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        Quantite quantiteActuel = adSupprimable.quantite("quantite");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(quantiteActuel),
                () -> Assertions.assertEquals(1, quantiteActuel.getValeur()),
                () -> Assertions.assertEquals(Mesure.MASSE, quantiteActuel.getMesure()),
                () -> Assertions.assertEquals(Mesure.MASSE.getUnite(true), quantiteActuel.getUnite())
        );
    }
}
