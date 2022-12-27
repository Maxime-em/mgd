package org.mgd.pam.zone;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.mgd.pam.commun.Bordure;
import org.mgd.pam.commun.Direction;
import org.mgd.pam.commun.Disposition;
import org.mgd.pam.commun.Echelonnement;
import org.mgd.pam.zone.exception.ZoneException;
import org.mgd.pam.zone.exception.ZoneStreamException;

public class FlotZone extends Zone {
    private final Direction direction;
    private final Echelonnement echelonnement;
    private final float longueur;
    private float espacementDebut;
    private float espacementInterne;

    public FlotZone(Direction direction, Echelonnement echelonnement, Disposition disposition, Zone parent, Bordure... bordures) {
        super(disposition, parent, bordures);
        this.direction = direction;
        this.echelonnement = echelonnement;
        this.longueur = direction == Direction.HORIZONTAL ? longueurX : longueurY;
        this.espacementDebut = echelonnement == Echelonnement.FIN ? this.longueur : 0.0f;
        this.espacementInterne = echelonnement == Echelonnement.ETENDU ? this.longueur : 0.0f;
    }

    private int pourmillesZones() {
        return enfants.stream()
                .map(zone -> direction == Direction.HORIZONTAL ? zone.disposition.getPourmilleX() : zone.disposition.getPourmilleY())
                .reduce(Integer::sum)
                .orElse(0);
    }

    private int pourmillesZones(int index) {
        return enfants.stream()
                .filter(zone -> direction == Direction.HORIZONTAL ? zone.getIndex() < index : zone.getIndex() > index)
                .map(zone -> direction == Direction.HORIZONTAL ? zone.disposition.getPourmilleX() : zone.disposition.getPourmilleY())
                .reduce(Integer::sum)
                .orElse(0);
    }

    private long nombreZones(int index) {
        return enfants.stream()
                .filter(zone -> direction == Direction.HORIZONTAL ? zone.getIndex() < index : zone.getIndex() > index)
                .count();
    }

    @Override
    protected final void dessiner(PDFont police, PDPageContentStream content) throws ZoneException {
        try {
            calculEspacementsCommuns();
            enfants.forEach(zone -> {
                try {
                    if (direction == Direction.HORIZONTAL) {
                        zone.placer(espacement(zone), 0);
                        zone.produire(police, content);
                    } else {
                        zone.placer(0, espacement(zone));
                        zone.produire(police, content);
                    }
                } catch (ZoneException e) {
                    throw new ZoneStreamException(e);
                }
            });
        } catch (Exception e) {
            throw new ZoneException(e);
        }
    }

    private void calculEspacementsCommuns() {
        switch (echelonnement) {
            case FIN -> espacementDebut = Math.max(0, 1000 - pourmillesZones()) * longueur / 1000;
            case REPARTI -> {
                espacementInterne = Math.max(0, 1000 - pourmillesZones()) * longueur / (1000 * (enfants.size() + 1));
                espacementDebut = espacementInterne;
            }
            case ETENDU ->
                    espacementInterne = Math.max(0, 1000 - pourmillesZones()) * longueur / (1000 * (enfants.size() - 1));
            case DEBUT -> { /* Rien à faire */ }
        }
    }

    private float espacement(Zone zone) {
        return espacementDebut + pourmillesZones(zone.getIndex()) * longueur / 1000 + nombreZones(zone.getIndex()) * espacementInterne;
    }
}
