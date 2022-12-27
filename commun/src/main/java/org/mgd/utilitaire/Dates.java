package org.mgd.utilitaire;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class Dates {
    private static final TemporalField champJourSemaine = WeekFields.of(Locale.FRANCE).dayOfWeek();

    private Dates() {
        throw new IllegalStateException("Classe utilitaire");
    }

    public static LocalDate decaler(LocalDate source, DayOfWeek cible) {
        return source.with(champJourSemaine, cible.getValue());
    }
}
