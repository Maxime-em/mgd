package org.mgd.gmel.pdf.pagination.zone;

import org.mgd.commun.TypeRepas;
import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.pam.commun.Bordure;
import org.mgd.pam.commun.Direction;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.commun.Echelonnement;
import org.mgd.pam.zone.*;
import org.mgd.temps.LocalRepas;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuZone extends FlotZone {
    private static final int POURMILLE_Y_ENTETE = 150;
    private static final int POURMILLE_Y_LIGNE = 100;
    private static final int POURMILLE_X_ENTETE_JOUR = 150;
    private static final int POURMILLE_X_ENTETE_REPAS = 350;
    private final Menu menu;

    public MenuZone(Menu menu, Disposition disposition, Zone parent, Bordure... bordures) {
        super(Direction.VERTICAL, Echelonnement.ETENDU, disposition, parent, bordures);
        this.menu = menu;
        creerLignes();
    }

    private void creerLignes() {
        LocalDate debut = LocalDate.now()
                .withYear(menu.getAnnee())
                .with(WeekFields.ISO.weekOfYear(), menu.getSemaine())
                .with(WeekFields.ISO.dayOfWeek(), DayOfWeek.MONDAY.getValue());

        LocalDate fin = LocalDate.now()
                .withYear(menu.getAnnee())
                .with(WeekFields.ISO.weekOfYear(), menu.getSemaine())
                .with(WeekFields.ISO.dayOfWeek(), DayOfWeek.SUNDAY.getValue())
                .plusDays(1);

        FlotZone entete = new FlotZone(Direction.HORIZONTAL, Echelonnement.REPARTI, new Disposition(0, 1000, POURMILLE_Y_ENTETE), this);
        new SemaineZone(new Disposition(0, POURMILLE_X_ENTETE_JOUR, 1000), entete);
        new RepasZone(TypeRepas.DEJEUNER, new Disposition(1, POURMILLE_X_ENTETE_REPAS, 1000), entete);
        new RepasZone(TypeRepas.DINER, new Disposition(2, POURMILLE_X_ENTETE_REPAS, 1000), entete);

        debut.datesUntil(fin).forEach(jour -> {
            FlotZone ligne = new FlotZone(Direction.HORIZONTAL, Echelonnement.REPARTI, new Disposition(jour.getDayOfWeek().getValue(), 1000, POURMILLE_Y_LIGNE), this);

            new JourZone(jour, new Disposition(0, POURMILLE_X_ENTETE_JOUR, 1000), ligne);

            FlotZone dejeunes = new FlotZone(Direction.VERTICAL, Echelonnement.REPARTI, new Disposition(1, POURMILLE_X_ENTETE_REPAS, 1000), ligne, Bordure.TOUTES);
            creerFormules(LocalRepas.pour(jour, TypeRepas.DEJEUNER), dejeunes);

            FlotZone diners = new FlotZone(Direction.VERTICAL, Echelonnement.REPARTI, new Disposition(2, POURMILLE_X_ENTETE_REPAS, 1000), ligne, Bordure.TOUTES);
            creerFormules(LocalRepas.pour(jour, TypeRepas.DINER), diners);
        });
    }

    private void creerFormules(LocalRepas repas, Zone parent) {
        AtomicInteger index = new AtomicInteger();
        long count = Math.max(menu.getFormules().stream().filter(formule -> formule.enVigueur(repas)).count(), 1);
        menu.getFormules()
                .stream()
                .filter(formule -> formule.enVigueur(repas))
                .forEach(formule -> new FormuleZone(formule, new Disposition(index.getAndIncrement(), 1000, (int) (1000 / count)), parent));
    }
}
