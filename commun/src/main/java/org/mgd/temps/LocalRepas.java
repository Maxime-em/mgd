package org.mgd.temps;

import com.google.gson.annotations.JsonAdapter;
import org.mgd.commun.TypeRepas;
import org.mgd.utilitaire.Dates;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@JsonAdapter(LocalRepasAdapter.class)
public class LocalRepas implements Comparable<LocalRepas> {
    private final LocalDate jour;
    private final TypeRepas type;

    private LocalRepas(LocalDate jour, TypeRepas type) {
        this.jour = jour;
        this.type = type;
    }

    public static LocalRepas pour(int year, int month, int dayOfMonth, TypeRepas type) {
        return creer(LocalDate.of(year, month, dayOfMonth), type);
    }

    public static LocalRepas pour(LocalDate jour, TypeRepas type) {
        return creer(jour, type);
    }

    public static LocalRepas depuis(LocalRepas source) {
        return creer(source.jour, source.type);
    }

    private static LocalRepas creer(LocalDate jour, TypeRepas type) {
        return new LocalRepas(jour, type);
    }

    /**
     * Nombre de demi-journées entre cette date de repas et celle donnée en argument.
     *
     * @param cible Date de repas cible comparaison.
     * @return Nombre de demi-journées.
     */
    public long nombreDemiesJournees(LocalRepas cible) {
        return ChronoUnit.DAYS.between(jour, cible.jour) * 2
                + (type == TypeRepas.DINER ? 0 : 1)
                - (cible.type != TypeRepas.DINER ? 1 : 0);
    }

    /**
     * Nombre de demi-journées entre cette date de repas et le diner du dernier jour de la semaine.
     *
     * @return Nombre de demi-journées.
     */
    public long nombreDemiesJournees() {
        return nombreDemiesJournees(LocalRepas.pour(Dates.decaler(jour, DayOfWeek.SUNDAY), TypeRepas.DINER));
    }

    public LocalDate getJour() {
        return jour;
    }

    public TypeRepas getType() {
        return type;
    }

    @Override
    public int compareTo(LocalRepas localRepas) {
        return jour.equals(localRepas.jour) ? type.compareTo(localRepas.type) : jour.compareTo(localRepas.jour);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalRepas that)) return false;
        return Objects.equals(jour, that.jour) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jour, type);
    }

    @Override
    public String toString() {
        return jour.toString() + type;
    }
}
