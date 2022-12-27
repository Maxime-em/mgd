package org.mgd.gmel.coeur;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mgd.gmel.coeur.objet.Bibliotheque;
import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.gmel.coeur.persistence.*;
import org.mgd.jab.JabSingletons;
import org.mgd.jab.exception.JabException;

import java.nio.file.Paths;
import java.time.LocalDate;

class JabmTest {
    private Jabm jab;

    @BeforeEach
    void setUp() throws JabException {
        JabSingletons.reinitialiser();

        jab = new Jabm(Paths.get("src/test/resources"));
    }

    @Test
    void instanciationObjet() {
        Assertions.assertAll(
                () -> Assertions.assertNotNull(jab.bibliotheque()),
                () -> Assertions.assertEquals(Bibliotheque.class, jab.bibliotheque().getClass()),
                () -> Assertions.assertNotNull(jab.epicerie()),
                () -> Assertions.assertEquals(Epicerie.class, jab.epicerie().getClass()),
                () -> Assertions.assertNotNull(jab.menu(LocalDate.of(2000, 1, 3))),
                () -> Assertions.assertEquals(Menu.class, jab.menu(LocalDate.of(2000, 1, 3)).getClass())
        );
    }

    @Test
    void instanciationJao() {
        Assertions.assertAll(
                () -> Assertions.assertNotNull(jab.bibliothequeJao()),
                () -> Assertions.assertEquals(BibliothequeJao.class, jab.bibliothequeJao().getClass()),
                () -> Assertions.assertNotNull(jab.epicerieJao()),
                () -> Assertions.assertEquals(EpicerieJao.class, jab.epicerieJao().getClass()),
                () -> Assertions.assertNotNull(jab.produitJao()),
                () -> Assertions.assertEquals(ProduitJao.class, jab.produitJao().getClass()),
                () -> Assertions.assertNotNull(jab.livreCuisineJao()),
                () -> Assertions.assertEquals(LivreCuisineJao.class, jab.livreCuisineJao().getClass()),
                () -> Assertions.assertNotNull(jab.quantiteJao()),
                () -> Assertions.assertEquals(QuantiteJao.class, jab.quantiteJao().getClass()),
                () -> Assertions.assertNotNull(jab.recetteJao()),
                () -> Assertions.assertEquals(RecetteJao.class, jab.recetteJao().getClass()),
                () -> Assertions.assertNotNull(jab.periodeJao()),
                () -> Assertions.assertEquals(PeriodeJao.class, jab.periodeJao().getClass()),
                () -> Assertions.assertNotNull(jab.formuleJao()),
                () -> Assertions.assertEquals(FormuleJao.class, jab.formuleJao().getClass()),
                () -> Assertions.assertNotNull(jab.menuJao()),
                () -> Assertions.assertEquals(MenuJao.class, jab.menuJao().getClass())
        );
    }
}
