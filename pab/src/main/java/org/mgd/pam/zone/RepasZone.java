package org.mgd.pam.zone;

import org.mgd.commun.TypeRepas;
import org.mgd.pam.commun.Bordure;
import org.mgd.pam.commun.Direction;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.commun.PoliceTailleStandard;

public class RepasZone extends TexteZone {
    public RepasZone(TypeRepas typeRepas, Disposition disposition, Zone parent, Bordure... bordures) {
        super(typeRepas.getLabel(), PoliceTailleStandard.GRANDE, Direction.HORIZONTAL, disposition, parent, bordures);
    }
}
