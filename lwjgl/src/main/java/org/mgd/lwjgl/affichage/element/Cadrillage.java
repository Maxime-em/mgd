package org.mgd.lwjgl.affichage.element;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.forme.Colorisation;
import org.mgd.lwjgl.forme.Forme;
import org.mgd.lwjgl.forme.Quadrilatere;
import org.mgd.utilitaire.Flux;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class Cadrillage extends Element<Integer[]> {
    private final Map<Forme, Integer[]> indexation;
    private final int replicationAbscisse;
    private final int replicationOrdonnee;

    public Cadrillage(Fenetre parent, String identifiant, int replicationAbscisse, int replicationOrdonnee, float z, Map<String, Path> textures) throws LwjglException {
        super(parent, identifiant,
                new float[]{-replicationAbscisse / 2f, -replicationOrdonnee / 2f, z},
                new float[]{1f, 1f, 1f},
                new float[]{0f, 0f, 0f},
                textures);
        this.replicationAbscisse = replicationAbscisse;
        this.replicationOrdonnee = replicationOrdonnee;
        this.indexation = Flux.fluxPairesEntiers(replicationAbscisse, replicationOrdonnee)
                .collect(Collectors.toMap(paire -> {
                    float abscisse = (float) paire[0] / replicationAbscisse;
                    float ordonnee = (float) paire[1] / replicationOrdonnee;
                    float constanteAbscisse = 1f / replicationAbscisse;
                    float constanteOrdonnee = (replicationOrdonnee - 1f) / replicationOrdonnee;
                    return new Quadrilatere(this, paire[1] + 1f, paire[0] + 1f, paire[1], paire[0], 0f,
                            new float[]{
                                    abscisse + constanteAbscisse, -ordonnee + constanteOrdonnee, 0f, 0f,
                                    abscisse, -ordonnee + constanteOrdonnee, 0f, 0f,
                                    abscisse, -ordonnee + 1f, 0f, 0f,
                                    abscisse + constanteAbscisse, -ordonnee + 1f, 0f, 0f
                            }, false, Colorisation.TEXTURE
                    );
                }, paire -> new Integer[]{replicationOrdonnee - paire[1] - 1, paire[0]}));
        this.formes = new ArrayList<>(indexation.keySet());
    }

    @Override
    public Integer[] elementActivation() {
        return indexation.get(formesActives.getFirst());
    }

    public float[] coordonnees(int ligne, int colonne) {
        ligne = Math.clamp(ligne, 0, replicationOrdonnee);
        colonne = Math.clamp(colonne, 0, replicationAbscisse);
        return new float[]{ligne + 1f, colonne + 1f, ligne, colonne, 0.5f};
    }
}
