package org.mgd.pam.zone;

import org.mgd.pam.commun.Bordure;
import org.mgd.pam.commun.Direction;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.commun.PoliceTailleStandard;
import org.mgd.utilitaire.Strings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class JourZone extends TexteZone {
    private static final DateTimeFormatter formateur = new DateTimeFormatterBuilder().appendPattern("EEEE d").toFormatter();

    public JourZone(LocalDate jour, Disposition disposition, Zone parent, Bordure... bordures) {
        super(Strings.premierCaractereMajuscule(jour.format(formateur)), PoliceTailleStandard.GRANDE, Direction.HORIZONTAL, disposition, parent, bordures);
    }
}
