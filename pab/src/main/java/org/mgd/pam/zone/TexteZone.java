package org.mgd.pam.zone;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;
import org.mgd.pam.commun.Bordure;
import org.mgd.pam.commun.Direction;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.commun.PoliceTailleStandard;
import org.mgd.pam.pagination.Paginateur;
import org.mgd.pam.zone.exception.ZoneException;

import java.io.IOException;

public class TexteZone extends Zone {
    private final String texte;
    private final PoliceTailleStandard policeTaille;
    private final Direction direction;

    protected TexteZone(String texte, PoliceTailleStandard policeTaille, Direction direction, Disposition disposition, Zone parent, Bordure... bordures) {
        super(disposition, parent, bordures);
        this.texte = texte;
        this.policeTaille = policeTaille;
        this.direction = direction;
    }

    @Override
    protected final void dessiner(PDFont police, PDPageContentStream content) throws ZoneException {
        try {
            float largeurTexte = police.getStringWidth(texte) * policeTaille.getValeur() / Paginateur.POLICE_UNITE_RATIO;
            float hauteurTexte = police.getFontDescriptor().getCapHeight() * policeTaille.getValeur() / Paginateur.POLICE_UNITE_RATIO;
            content.beginText();
            content.setFont(police, policeTaille.getValeur());
            if (direction == Direction.HORIZONTAL) {
                content.setTextMatrix(new Matrix(1, 0, 0, 1, (longueurX - largeurTexte) / 2, (longueurY - hauteurTexte) / 2));
            } else {
                content.setTextMatrix(new Matrix(0, 1, -1, 0, (longueurX + hauteurTexte) / 2, (longueurY - largeurTexte) / 2));
            }
            content.showText(texte);
            content.endText();
        } catch (IOException e) {
            throw new ZoneException(e);
        }
    }
}
