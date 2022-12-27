package org.mgd.gmel.pdf.pagination.zone;

import org.mgd.gmel.coeur.objet.Formule;
import org.mgd.pam.commun.Bordure;
import org.mgd.pam.commun.Direction;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.commun.PoliceTailleStandard;
import org.mgd.pam.zone.TexteZone;
import org.mgd.pam.zone.Zone;

public class FormuleZone extends TexteZone {
    public FormuleZone(Formule formule, Disposition disposition, Zone parent, Bordure... bordures) {
        super(formule.getRecette().getNom(), PoliceTailleStandard.NORMALE, Direction.HORIZONTAL, disposition, parent, bordures);
    }
}
