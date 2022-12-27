package org.mgd.jab.objet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mgd.jab.persistence.JeuType;

import java.util.UUID;

class JoTest {
    private static Pegi pegi12;
    private static Jeu jeu1;

    @BeforeEach
    void setUp() {
        pegi12 = new Pegi();
        pegi12.setIdentifiant(UUID.fromString("954f6c4b-ff8b-4b7e-8ea0-759e1df0ccfc"));
        pegi12.setAge(18);

        jeu1 = new Jeu();
        jeu1.setIdentifiant(UUID.fromString("ffa4c46a-5898-4b7c-9162-91971cc8fa44"));
        jeu1.setNom("Jeu 1");
        jeu1.setAnnee(2024);
        jeu1.setSemaine(1);
        jeu1.setType(JeuType.CARTES);
        jeu1.setPegi(pegi12);
    }

    @Test
    void idem() {
        Assertions.assertTrue(jeu1.idem(jeu1));

        Jeu jeu2 = new Jeu();
        jeu2.setIdentifiant(UUID.fromString("ffa4c46a-5898-4b7c-9162-91971cc8fa44"));
        jeu2.setNom("Jeu 2");
        jeu2.setAnnee(2024);
        jeu2.setSemaine(1);
        jeu2.setType(JeuType.CARTES);
        jeu2.setPegi(pegi12);

        Assertions.assertAll(
                () -> Assertions.assertNotSame(jeu1, jeu2),
                () -> Assertions.assertEquals(jeu1, jeu2),
                () -> Assertions.assertFalse(jeu1.idem(jeu2))
        );

        Jeu jeu3 = new Jeu();
        jeu3.setIdentifiant(UUID.fromString("bc24b033-e2a3-415c-b50a-305f2ae9d627"));
        jeu3.setNom("Jeu 1");
        jeu3.setAnnee(2024);
        jeu3.setSemaine(1);
        jeu3.setType(JeuType.CARTES);
        jeu3.setPegi(pegi12);

        Assertions.assertAll(
                () -> Assertions.assertNotSame(jeu1, jeu3),
                () -> Assertions.assertNotEquals(jeu1, jeu3),
                () -> Assertions.assertTrue(jeu1.idem(jeu3))
        );
    }
}
