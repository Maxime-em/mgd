package org.mgd.lwjgl.element;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.forme.Case;
import org.mgd.lwjgl.forme.Colorisation;
import org.mgd.lwjgl.forme.Forme;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Actions extends Element {
    private final Map<Forme, Integer> indexation;
    private final LinkedList<ActivationAction> activations;

    public Actions(Fenetre parent, String identifiant, int nombre, float hauteur, float largeur, float[] marges, Map<String, String> textures) throws LwjglException {
        super(parent, identifiant, new float[]{0f, 0f, 0f}, new float[]{1f, 1f, 1f}, new float[]{0f, 0f, 0f}, textures);
        marger(marges);
        this.activations = new LinkedList<>();
        this.indexation = IntStream.range(0, nombre).boxed().collect(Collectors.toMap(index -> {
            float couleur = (float) index / nombre;
            float decalage = (2f - margeDroite() - margeGauche() - nombre * largeur) / (nombre - 1);
            return new Case(this,
                    -1f + margeHaut() + hauteur,
                    -1f + margeDroite() + (index + 1) * largeur + index * decalage,
                    -1f + margeBasse(),
                    -1f + margeGauche() + index * (largeur + decalage),
                    0,
                    new float[]{
                            couleur, couleur, 0f, 1f,
                            couleur, couleur, 0f, 1f,
                            couleur, couleur, 0.5f, 1f,
                            couleur, couleur, 0.5f, 1f
                    },
                    Colorisation.COULEUR);
        }, Function.identity()));
        this.formes = new ArrayList<>(indexation.keySet());
    }

    public Actions souscrire(ActivationAction action) {
        activations.add(action);
        return this;
    }

    @Override
    public void activer(Fenetre.Souris souris) {
        super.activer(souris);
        formesActives.forEach(forme -> {
            activations.forEach(activation -> activation.traiter(indexation.get(forme)));
            forme.desactiver();
        });
    }
}
