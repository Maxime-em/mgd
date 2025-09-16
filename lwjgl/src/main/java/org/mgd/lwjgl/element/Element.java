package org.mgd.lwjgl.element;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.forme.Forme;
import org.mgd.lwjgl.interne.Ombreur;
import org.mgd.lwjgl.interne.Tisseur;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Element {
    protected final Fenetre parent;
    private final String identifiant;
    private final Matrice<Float> transformation;
    protected List<Forme> formes;
    protected List<Forme> formesActives;
    protected float[] marges;

    public Element(Fenetre parent, String identifiant, float[] translation, float[] agrandissement, float[] rotation, Map<String, String> textures) throws LwjglException {
        this.parent = parent;
        this.identifiant = identifiant;
        this.transformation = Matrice.transformation(translation, agrandissement, rotation);
        this.formes = Collections.emptyList();
        this.formesActives = Collections.emptyList();
        this.marges = new float[4];
        if (!textures.isEmpty()) {
            Tisseur.compiler(identifiant, textures);
        }
        parent.enfants().add(this);
    }

    public void survoler(Fenetre.Souris souris) {
        formes.forEach(forme -> forme.survoler(souris, transformation));
    }

    public void activer(Fenetre.Souris souris) {
        formes.forEach(forme -> forme.activer(souris));
        formesActives = formes.stream().filter(Forme::active).toList();
    }

    public void produire(Fenetre.Souris souris) {
        Ombreur.specifier("transformation", transformation);
        survoler(souris);
        if (souris.selection()) {
            activer(souris);
        }
        formes.forEach(Forme::produire);
    }

    public void nettoyer() {
        formes.forEach(Forme::nettoyer);
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

    public String getIdentifiant() {
        return identifiant;
    }
}
