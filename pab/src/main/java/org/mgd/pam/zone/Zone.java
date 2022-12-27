package org.mgd.pam.zone;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;
import org.mgd.pam.commun.Bordure;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.zone.exception.ZoneException;
import org.mgd.pam.zone.exception.ZoneStreamException;

import java.io.IOException;
import java.util.*;

public class Zone {
    protected final Set<Zone> enfants;
    protected final Disposition disposition;
    protected final float longueurX;
    protected final float longueurY;
    private final Zone parent;
    private final Bordure[] bordures;

    private Zone(Disposition disposition, float longueurX, float longueurY, Bordure... bordures) {
        this.parent = null;
        this.enfants = new HashSet<>();
        this.disposition = disposition;
        this.longueurX = longueurX;
        this.longueurY = longueurY;
        this.bordures = bordures;
    }

    protected Zone(Disposition disposition, Zone parent, Bordure... bordures) {
        Objects.requireNonNull(parent);

        this.parent = parent;
        this.enfants = new HashSet<>();
        this.disposition = disposition;
        this.longueurX = disposition.getPourmilleX() * parent.longueurX / 1000;
        this.longueurY = disposition.getPourmilleY() * parent.longueurY / 1000;
        this.bordures = bordures;

        parent.enfants.add(this);
    }

    public static Zone racine(float longueurX, float longueurY) {
        return new Zone(new Disposition(0, 1000, 1000), longueurX, longueurY);
    }

    protected void dessiner(PDFont police, PDPageContentStream content) throws ZoneException {
        try {
            enfants.forEach(enfant -> {
                try {
                    enfant.produire(police, content);
                } catch (ZoneException e) {
                    throw new ZoneStreamException(e);
                }
            });
        } catch (ZoneStreamException e) {
            throw new ZoneException(e);
        }
    }

    public void produire(PDFont police, PDPageContentStream content) throws ZoneException {
        try {
            content.transform(new Matrix(1, 0, 0, 1, disposition.getX(), disposition.getY()));
            dessiner(police, content);
            tracerBordures(content, bordures);
            content.transform(new Matrix(1, 0, 0, 1, -disposition.getX(), -disposition.getY()));
        } catch (IOException e) {
            throw new ZoneException(e);
        }
    }

    public void tracerBordures(PDPageContentStream content, Bordure... bordures) throws IOException {
        List<Bordure> liste = Arrays.asList(bordures);
        boolean toutes = liste.contains(Bordure.TOUTES);
        content.moveTo(0, 0);
        if (toutes || liste.contains(Bordure.GAUCHE)) {
            content.lineTo(0, longueurY);
        } else {
            content.moveTo(0, longueurY);
        }
        if (toutes || liste.contains(Bordure.HAUTE)) {
            content.lineTo(longueurX, longueurY);
        } else {
            content.moveTo(longueurX, longueurY);
        }
        if (toutes || liste.contains(Bordure.DROITE)) {
            content.lineTo(longueurX, 0);
        } else {
            content.moveTo(longueurX, 0);
        }
        if (toutes || liste.contains(Bordure.BASSE)) {
            content.lineTo(0, 0);
        } else {
            content.moveTo(0, 0);
        }
        content.stroke();
    }

    public void placer(float x, float y) {
        disposition.setX(x);
        disposition.setY(y);
    }

    public int getIndex() {
        return disposition.getIndex();
    }

    public Zone getParent() {
        return parent;
    }
}
