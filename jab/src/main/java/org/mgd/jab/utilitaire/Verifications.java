package org.mgd.jab.utilitaire;

import org.mgd.jab.utilitaire.exception.VerificationException;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.IsoFields;
import java.util.Collection;
import java.util.function.BiPredicate;

public class Verifications {
    private Verifications() {
        throw new IllegalStateException("Classe utilitaire.");
    }

    public static void nonNull(Object objet, String message, Object... objects) throws VerificationException {
        if (objet == null) {
            throw new VerificationException(MessageFormat.format(message, objects));
        }
    }

    public static void nonVide(String chaine, String message) throws VerificationException {
        if (chaine == null || chaine.isBlank()) {
            throw new VerificationException(MessageFormat.format(message, chaine));
        }
    }

    public static void nonNegatif(Integer nombre, String message) throws VerificationException {
        if (nombre == null || nombre < 1) {
            throw new VerificationException(MessageFormat.format(message, nombre));
        }
    }

    public static void nonNegatif(Long nombre, String message) throws VerificationException {
        if (nombre == null || nombre < 1) {
            throw new VerificationException(MessageFormat.format(message, nombre));
        }
    }

    public static void nonStrictementNegatif(Long nombre, String message) throws VerificationException {
        if (nombre == null || nombre < 0) {
            throw new VerificationException(MessageFormat.format(message, nombre));
        }
    }

    public static void nonBorne(Long nombre, Long minimum, Long maximum, String message) throws VerificationException {
        if (nombre == null || nombre < minimum || nombre > maximum) {
            throw new VerificationException(MessageFormat.format(message, nombre, minimum, maximum));
        }
    }

    public static void nonBorne(Integer nombre, Integer minimum, Integer maximum, String message) throws VerificationException {
        nonBorne(Long.valueOf(nombre), Long.valueOf(minimum), Long.valueOf(maximum), message);
    }

    public static void nonAnnee(Integer nombre, String message) throws VerificationException {
        nonBorne(Long.valueOf(nombre), Long.valueOf(LocalDate.MIN.getYear()), Long.valueOf(LocalDate.MAX.getYear()), message);
    }

    public static void nonSemaine(Integer nombre, Integer annee, String message) throws VerificationException {
        nonBorne(Long.valueOf(nombre), 1L, LocalDate.of(annee, Month.DECEMBER, 1).range(IsoFields.WEEK_OF_WEEK_BASED_YEAR).getMaximum(), message);
    }

    public static <T> void nonMultiple(Collection<T> objets, BiPredicate<T, T> predicat, String message) throws VerificationException {
        if (objets == null || objets.stream().anyMatch(objet1 -> objets.stream().filter(objet2 -> predicat.test(objet1, objet2)).count() > 1L)) {
            throw new VerificationException(MessageFormat.format(message, objets));
        }
    }
}
