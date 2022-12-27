package org.mgd.pam.zone;

import org.mgd.pam.commun.Bordure;
import org.mgd.pam.commun.Direction;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.commun.PoliceTailleStandard;

public class SemaineZone extends TexteZone {
    public SemaineZone(Disposition disposition, Zone parent, Bordure... bordures) {
        super("Semaine", PoliceTailleStandard.GRANDE, Direction.HORIZONTAL, disposition, parent, bordures);
    }
}
