package org.mgd.lwjgl.element;

import org.mgd.commun.IConstantesMathematiques;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.forme.Case;
import org.mgd.lwjgl.forme.Colorisation;
import org.mgd.lwjgl.forme.Forme;
import org.mgd.lwjgl.interne.Tisseur;
import org.mgd.utilitaire.Flux;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Cadrillage extends Element {
    private final List<SelectionCase> selectionCases;
    private final List<DeselectionCase> deselectionCases;
    private final Map<Forme, Integer[]> indexation;

    public Cadrillage(Fenetre parent, String identifiant, int replicationAbscisse, int replicationOrdonnee, Map<String, String> textures) throws LwjglException {
        super(parent, identifiant,
                new float[]{
                        -replicationAbscisse / 2f,
                        -replicationOrdonnee / 2f,
                        -Math.max(IConstantesMathematiques.RACINE_TROIS * parent.ratioDenominateur() * replicationAbscisse / (2f * parent.ratioNumerateur()), IConstantesMathematiques.RACINE_TROIS * replicationOrdonnee / 2f)
                },
                new float[]{1f, 1f, 1f},
                new float[]{0f, 0f, 0f},
                textures);
        this.selectionCases = new LinkedList<>();
        this.deselectionCases = new LinkedList<>();
        this.indexation = Flux.fluxPairesEntiers(replicationAbscisse, replicationOrdonnee)
                .collect(Collectors.toMap(paire -> {
                    boolean avecTexture = textures.keySet().stream().anyMatch(pseudo -> Objects.equals(pseudo, Tisseur.PSEUDO_BASE));
                    float abscisse = (float) paire[0] / replicationAbscisse;
                    float ordonnee = (float) paire[1] / replicationOrdonnee;
                    float constanteAbscisse = 1f / replicationAbscisse;
                    float constanteOrdonnee = (replicationOrdonnee - 1f) / replicationOrdonnee;
                    return new Case(this,
                            paire[1] + 1f,
                            paire[0] + 1f,
                            paire[1],
                            paire[0],
                            0f, new float[]{
                            abscisse + constanteAbscisse, -ordonnee + constanteOrdonnee, 0f, 0f,
                            abscisse, -ordonnee + constanteOrdonnee, 0f, 0f,
                            abscisse, -ordonnee + 1f, 0f, 0f,
                            abscisse + constanteAbscisse, -ordonnee + 1f, 0f, 0f
                    },
                            false,
                            avecTexture ? Colorisation.TEXTURE : Colorisation.COULEUR
                    );
                }, Function.identity()));
        this.formes = new ArrayList<>(indexation.keySet());
    }

    public Cadrillage souscrire(SelectionCase selectionCase) {
        selectionCases.add(selectionCase);
        return this;
    }

    public Cadrillage souscrire(DeselectionCase deselectionCase) {
        deselectionCases.add(deselectionCase);
        return this;
    }

    @Override
    public void activer(Fenetre.Souris souris) {
        super.activer(souris);
        if (formesActives.isEmpty()) {
            deselectionCases.forEach(DeselectionCase::traiter);
        } else {
            formesActives.forEach(forme -> selectionCases.forEach(selectionCase -> {
                Integer[] index = indexation.get(forme);
                selectionCase.traiter(index[1], index[0]);
            }));
        }
    }
}
