package org.mgd.lwjgl.element;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.forme.Forme;
import org.mgd.lwjgl.interne.Ombreur;
import org.mgd.lwjgl.interne.Tisseur;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public abstract class Element extends Primitif implements Producteur {
    protected final Fenetre parent;
    private final String identifiant;
    protected final Matrice<Float> transformation;
    protected List<Forme> formes;
    protected float[] marges;

    public Element(Fenetre parent, String identifiant, float[] translation, float[] agrandissement, float[] rotation, Map<String, String> textures) throws LwjglException {
        this.parent = parent;
        this.identifiant = identifiant;
        this.transformation = Matrice.transformation(translation, agrandissement, rotation);
        this.formes = Collections.emptyList();
        this.marges = new float[4];
        if (!textures.isEmpty()) {
            Tisseur.compiler(identifiant, textures);
        }
        parent.enfants().add(this);
    }

    @Override
    public void produire(Vision vision) {
        if (visible) {
            Ombreur.specifier("transformation", transformation);
            formes.forEach(Forme::produire);
        }
    }

    public void nettoyer() {
        formes.forEach(Forme::nettoyer);
    }

    protected void placer(float decalagex, float decalagey) {
        transformation.modifierValeur(0, 3, decalagex, (ancienne, nouvelle) -> nouvelle);
        transformation.modifierValeur(1, 3, decalagey, (ancienne, nouvelle) -> nouvelle);
    }

    protected void marger(float[] marges) {
        IntStream.range(0, this.marges.length).forEach(i -> this.marges[i] = marges.length > i ? marges[i] : 0f);
    }

    public float margeHaut() {
        return marges[0];
    }

    public float margeDroite() {
        return marges[1];
    }

    public float margeBasse() {
        return marges[2];
    }

    public float margeGauche() {
        return marges[3];
    }

    public String identifiant() {
        return identifiant;
    }

    public float minimumz() {
        return formes.stream().map(Forme::minimumz).min(Float::compare).orElse(Float.MIN_VALUE);
    }
}
