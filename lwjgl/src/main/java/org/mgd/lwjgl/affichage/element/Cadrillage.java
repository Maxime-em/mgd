package org.mgd.lwjgl.affichage.element;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.affichage.element.forme.Forme;
import org.mgd.lwjgl.affichage.element.forme.Quadrilatere;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.utilitaire.Flux;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Cadrillage extends Element<String> {
    private static final String GROUPE_CASES = "cases";
    private static final String GROUPE_JETONS = "jetons";
    private static final Map<Integer, Float[][]> COORDONNEES_CENTRES_GRAVITES_JETONS = Map.of(
            1, new Float[][]{{0.5f, 0.5f}},
            2, new Float[][]{{0.75f, 0.75f}, {0.25f, 0.25f}},
            3, new Float[][]{{0.75f, 0.384f}, {0.384f, 0.75f}, {0.25f, 0.25f}},
            4, new Float[][]{{0.75f, 0.25f}, {0.75f, 0.75f}, {0.25f, 0.75f}, {0.25f, 0.25f}},
            5, new Float[][]{{0.605f, 0.197f}, {0.797f, 0.526f}, {0.544f, 0.81f}, {0.196f, 0.658f}, {0.233f, 0.279f}}
    );

    private final int largeurCarte;
    private final int largeurJeton;
    private final int hauteurJeton;
    private final int largeur;
    private final int hauteur;
    private final int nombreLignes;
    private final Map<Forme, Integer[]> indexParCase;
    private final Map<Forme, Integer[]> indexParJetons;

    public Cadrillage(Fenetre parent, int priorite, int[] tailleCadrillage, int[] tailleJetons, float[] translation, Map<String, Path> textures) throws LwjglException {
        super(parent,
                "cadrillage",
                priorite,
                translation,
                new float[]{1f, 1f, 1f},
                new float[]{0f, 0f, 0f},
                textures);
        this.largeurCarte = tailleCadrillage[2];
        this.largeurJeton = tailleJetons[2];
        this.hauteurJeton = tailleJetons[3];
        this.largeur = largeurCarte + tailleJetons[1] * largeurJeton;
        this.hauteur = Math.max(tailleCadrillage[3], tailleJetons[0] * hauteurJeton);
        this.nombreLignes = tailleCadrillage[0];
        int largeurCase = largeurCarte / tailleCadrillage[1];
        int hauteurCase = tailleCadrillage[3] / nombreLignes;
        this.indexParCase = Flux.fluxPairesEntiers(tailleCadrillage[1], nombreLignes)
                .collect(Collectors.toMap(index -> new Quadrilatere(this,
                                "case",
                                index[1] + 1f,
                                index[0] + 1f,
                                index[1],
                                index[0],
                                0f,
                                new float[]{
                                        (float) (index[0] + 1) * largeurCase / this.largeur, (float) (nombreLignes - index[1] - 1) * hauteurCase / this.hauteur,
                                        (float) index[0] * largeurCase / this.largeur, (float) (nombreLignes - index[1] - 1) * hauteurCase / this.hauteur,
                                        (float) index[0] * largeurCase / this.largeur, (float) (nombreLignes - index[1]) * hauteurCase / this.hauteur,
                                        (float) (index[0] + 1) * largeurCase / this.largeur, (float) (nombreLignes - index[1]) * hauteurCase / this.hauteur
                                }),
                        index -> new Integer[]{nombreLignes - index[1] - 1, index[0]}));
        ajouter(GROUPE_CASES, this.indexParCase.keySet());
        this.indexParJetons = new HashMap<>();
    }

    private static String genererGroupeJetons(Integer ligneCase, Integer colonneCase) {
        return MessageFormat.format("{0}-{1}-{2}", GROUPE_JETONS, ligneCase, colonneCase);
    }

    private void ajuster(Integer ligneCase, Integer colonneCase, List<Forme> formes) {
        int nombreFormes = formes.size();
        if (nombreFormes > 0) {
            Float[][] centres = COORDONNEES_CENTRES_GRAVITES_JETONS.get(Math.min(COORDONNEES_CENTRES_GRAVITES_JETONS.size(), nombreFormes));
            AtomicInteger index = new AtomicInteger(0);
            formes.stream()
                    .limit(centres.length)
                    .forEach(forme -> {
                        Float[] coordonnees = centres[index.getAndIncrement()];
                        forme.deplacer(new float[]{colonneCase + coordonnees[0], nombreLignes - 1 - ligneCase + coordonnees[1], 0f}, 1_000);
                    });
        }
    }

    public Forme ajouterJeton(Integer ligneCase, Integer colonneCase, int ligneJeton, int colonneJeton) {
        Quadrilatere jeton = new Quadrilatere(this,
                "jeton",
                (ligneCase + 1f) * 0.25f + ligneCase * 0.75f,
                (colonneCase + 1f) * 0.25f + colonneCase * 0.75f,
                ligneCase,
                colonneCase,
                0f,
                new float[]{
                        (float) (largeurCarte + (colonneJeton + 1) * largeurJeton) / largeur, (float) (ligneJeton * hauteurJeton) / hauteur,
                        (float) (largeurCarte + colonneJeton * largeurJeton) / largeur, (float) (ligneJeton * hauteurJeton) / hauteur,
                        (float) (largeurCarte + colonneJeton * largeurJeton) / largeur, (float) ((ligneJeton + 1) * hauteurJeton) / hauteur,
                        (float) (largeurCarte + (colonneJeton + 1) * largeurJeton) / largeur, (float) ((ligneJeton + 1) * hauteurJeton) / hauteur
                });
        ajuster(ligneCase, colonneCase, ajouter(genererGroupeJetons(ligneCase, colonneCase), jeton));
        this.indexParJetons.put(jeton, new Integer[]{ligneCase, colonneCase});
        return jeton;
    }

    public Map<Forme, Integer[]> indexParCase() {
        return this.indexParCase;
    }

    public void desactiverJetons() {
        indexParJetons.keySet().forEach(Forme::desactiver);
    }

    public void deplacer(Forme jeton, Integer ligneCase, Integer colonneCase) {
        Integer[] index = indexParJetons.get(jeton);
        ajuster(index[0], index[1], supprimer(genererGroupeJetons(index[0], index[1]), jeton));
        ajuster(ligneCase, colonneCase, ajouter(genererGroupeJetons(ligneCase, colonneCase), jeton));
        indexParJetons.put(jeton, new Integer[]{ligneCase, colonneCase});
    }
}
