package org.mgd.lwjgl.affichage.tetehaute.composant;

import org.mgd.lwjgl.Survolable;
import org.mgd.lwjgl.souscription.Identifiable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Liste<T> implements Identifiable {
    private final UUID uuid;
    private final List<ActionTextutelle<T>> actionTextutelles;
    private final List<Survolable> liaisons;
    private final float[] dimensions;
    private final int espacement;
    private final int marge;
    private int abscisse;
    private int ordonnee;

    @SafeVarargs
    public Liste(int espacement, int marge, ActionTextutelle<T>... actionTextutelles) {
        this.uuid = UUID.randomUUID();
        this.actionTextutelles = Arrays.asList(actionTextutelles);
        this.liaisons = new LinkedList<>();
        this.dimensions = new float[4];
        this.espacement = espacement;
        this.marge = marge;
    }

    public void dimensionner(long contexte) {
        AtomicBoolean premier = new AtomicBoolean(true);
        actionTextutelles.forEach(action -> {
            action.dimensionner(contexte);
            float[] dimensionsAction = action.dimensions();
            if (premier.compareAndSet(true, false)) {
                dimensions[0] = dimensionsAction[0];
                dimensions[1] = dimensionsAction[1];
                dimensions[2] = dimensionsAction[2];
                dimensions[3] = dimensionsAction[3];
            } else {
                dimensions[0] = Math.min(dimensions[0], dimensionsAction[0]);
                dimensions[1] = Math.min(dimensions[1], dimensionsAction[1]);
                dimensions[2] = Math.max(dimensions[2], dimensionsAction[2]);
                dimensions[3] = dimensions[3] + espacement + dimensionsAction[3] - dimensionsAction[1];
            }
        });
        dimensions[2] += 2 * marge;
        actionTextutelles.forEach(action -> action.elargir(largeur()));
    }

    public void placer(int abscisse, int ordonnee) {
        this.abscisse = abscisse;
        this.ordonnee = ordonnee;
        AtomicInteger ordonneeCourante = new AtomicInteger();
        this.actionTextutelles.forEach(action -> action.placer(abscisse + marge, ordonnee + ordonneeCourante.getAndAdd(action.hauteur() + espacement)));
    }

    public void lier(Survolable liaison) {
        this.liaisons.add(liaison);
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    public List<ActionTextutelle<T>> actions() {
        return actionTextutelles;
    }

    public List<Survolable> liaisons() {
        return liaisons;
    }

    public int largeur() {
        return (int) Math.ceil(dimensions[2] - dimensions[0]);
    }

    public int hauteur() {
        return (int) Math.ceil(dimensions[3] - dimensions[1]);
    }

    public int abscisse() {
        return abscisse;
    }

    public int ordonnee() {
        return ordonnee;
    }
}
