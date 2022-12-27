package org.mgd.gmel.pdf.pagination.producteur;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.gmel.pdf.pagination.zone.MenuZone;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.pagination.Paginateur;
import org.mgd.pam.producteur.Producteur;
import org.mgd.pam.producteur.exception.ProducteurException;
import org.mgd.pam.zone.Zone;
import org.mgd.pam.zone.exception.ZoneException;

public class MenuProducteur extends Producteur<Menu> {
    @Override
    public void produire(Menu element, PDFont police, PDPageContentStream content, Zone racine) throws ProducteurException {
        try {
            new MenuZone(element, new Disposition(0, 1000, 1000, Paginateur.LONGUEUR_MARGE_DEFAUT, Paginateur.LONGUEUR_MARGE_DEFAUT), racine)
                    .getParent()
                    .produire(police, content);
        } catch (ZoneException e) {
            throw new ProducteurException(e);
        }
    }
}
