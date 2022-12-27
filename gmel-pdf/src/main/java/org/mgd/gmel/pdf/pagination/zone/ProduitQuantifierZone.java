package org.mgd.gmel.pdf.pagination.zone;

import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.pam.commun.Bordure;
import org.mgd.pam.commun.Direction;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.commun.PoliceTailleStandard;
import org.mgd.pam.zone.TexteZone;
import org.mgd.pam.zone.Zone;

import java.text.MessageFormat;

public class ProduitQuantifierZone extends TexteZone {
    protected ProduitQuantifierZone(ProduitQuantifier produitQuantifier, Disposition disposition, Zone parent, Bordure... bordures) {
        super(MessageFormat.format("{0}: {1} {2}",
                        produitQuantifier.getProduit().getNom(),
                        produitQuantifier.getQuantite().getValeur(),
                        produitQuantifier.getQuantite().getUnite()),
                PoliceTailleStandard.GRANDE,
                Direction.VERTICAL,
                disposition,
                parent,
                bordures
        );
    }
}
