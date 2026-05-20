package org.mgd.jab.utilitaire;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Tests des vérifications avec des valeurs liées à l'univers DC Comics.
 */
public class VerificationsTest {

    @Test
    void nonNullAccepteUnObjet() {
        Assertions.assertDoesNotThrow(() -> Verifications.nonNull("Batman", "Absent."));
    }

    @Test
    void nonNullRefuseNullAvecMessage() {
        VerificationException exception = Assertions.assertThrows(VerificationException.class,
                () -> Verifications.nonNull(null, "Le héros {0} est absent.", "Robin"));

        Assertions.assertEquals("Le héros Robin est absent.", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void nonVideRefuseNullEtVide(String chaine) {
        Assertions.assertThrows(VerificationException.class, () -> Verifications.nonVide(chaine, "Nom vide."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Batman", "Wonder Woman"})
    void nonVideAccepteUnNom(String nom) {
        Assertions.assertDoesNotThrow(() -> Verifications.nonVide(nom, "Nom vide."));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 5, Long.MAX_VALUE})
    void nonNegatifAccepteLesPositifs(long valeur) {
        Assertions.assertDoesNotThrow(() -> Verifications.nonNegatif(valeur, "Négatif."));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    void nonNegatifRefuseZeroEtNegatifs(long valeur) {
        Assertions.assertThrows(VerificationException.class, () -> Verifications.nonNegatif(valeur, "Négatif."));
    }

    @Test
    void nonNegatifRefuseNull() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(VerificationException.class,
                        () -> Verifications.nonNegatif((Integer) null, "Nul.")),
                () -> Assertions.assertThrows(VerificationException.class,
                        () -> Verifications.nonNegatif((Long) null, "Nul."))
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 100})
    void nonStrictementNegatifAccepteZeroEtPositifs(long valeur) {
        Assertions.assertDoesNotThrow(() -> Verifications.nonStrictementNegatif(valeur, "Négatif."));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, Long.MIN_VALUE})
    void nonStrictementNegatifRefuseLesNegatifs(long valeur) {
        Assertions.assertThrows(VerificationException.class,
                () -> Verifications.nonStrictementNegatif(valeur, "Négatif."));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 5, 10})
    void nonBorneAccepteLesBornesInclues(long valeur) {
        Assertions.assertDoesNotThrow(() -> Verifications.nonBorne(valeur, 1L, 10L, "Hors bornes."));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 11, -5})
    void nonBorneRefuseHorsBornes(long valeur) {
        Assertions.assertThrows(VerificationException.class,
                () -> Verifications.nonBorne(valeur, 1L, 10L, "Hors bornes."));
    }

    @Test
    void classeUtilitaireNonInstanciable() throws NoSuchMethodException {
        Constructor<Verifications> constructeur = Verifications.class.getDeclaredConstructor();
        constructeur.setAccessible(true);

        InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class,
                constructeur::newInstance);

        Assertions.assertEquals("Classe utilitaire.", exception.getCause().getMessage());
    }
}
