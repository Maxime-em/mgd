package org.mgd.gmel.pdf.pagination.zone;

import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.gmel.pdf.pagination.InventairePaginateur;
import org.mgd.pam.commun.Bordure;
import org.mgd.pam.commun.Direction;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.commun.Echelonnement;
import org.mgd.pam.zone.FlotZone;
import org.mgd.pam.zone.Zone;

import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

public class ProduitQuantifierFlotZone extends FlotZone {
    private final SortedSet<ProduitQuantifier> produitsQuantifier;

    public ProduitQuantifierFlotZone(SortedSet<ProduitQuantifier> produitsQuantifier, Disposition disposition, Zone parent, Bordure... bordures) {
        super(Direction.HORIZONTAL, Echelonnement.DEBUT, disposition, parent, bordures);
        this.produitsQuantifier = produitsQuantifier;
        creerLignes();
    }

    private void creerLignes() {
        AtomicInteger index = new AtomicInteger();
        produitsQuantifier.forEach(produitQuantifier -> new ProduitQuantifierZone(produitQuantifier, new Disposition(index.getAndIncrement(), 1000 / InventairePaginateur.NOMBRE_MAX_PRODUITS_QUANTIFIER_PAR_PAGE, 1000), this));
    }
}
