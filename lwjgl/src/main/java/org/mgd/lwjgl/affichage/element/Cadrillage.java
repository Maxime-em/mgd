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
import java.util.function.Function;
import java.util.stream.Collectors;

public class Cadrillage extends ElementAnimateur<Integer[]> {
    private final int replicationAbscisse;
    private final int replicationOrdonnee;
    private final Map<Forme, Integer[]> indexation;

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
                }, Function.identity()));
        this.formes = new ArrayList<>(indexation.keySet());
    }

    public void accuellir(Jeton jeton, int ligne, int colonne) {
        if (jeton != null) {
            jeton.placer(colonne - replicationAbscisse / 2f, ligne - replicationOrdonnee / 2f);
        }
    }

    @Override
    public Integer[] elementActivation() {
        return indexation.get(formesActives.getFirst());
    }
}
