package org.mgd.lwjgl.affichage.element;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.affichage.element.forme.Maillage;
import org.mgd.lwjgl.affichage.element.forme.Quadrilatere;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.utilitaire.Flux;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Quadrillage extends GroupeFormes<String> {
    private static final String GROUPE_CASES = "cases";
    private static final String GROUPE_JETONS = "jetons";
    private static final Map<Long, Float[][]> COORDONNEES_CENTRES_GRAVITES_JETONS = Map.of(
            1L, new Float[][]{{0.5f, 0.5f}},
            2L, new Float[][]{{0.75f, 0.75f}, {0.25f, 0.25f}},
            3L, new Float[][]{{0.75f, 0.384f}, {0.384f, 0.75f}, {0.25f, 0.25f}},
            4L, new Float[][]{{0.75f, 0.25f}, {0.75f, 0.75f}, {0.25f, 0.75f}, {0.25f, 0.25f}},
            5L, new Float[][]{{0.605f, 0.197f}, {0.797f, 0.526f}, {0.544f, 0.81f}, {0.196f, 0.658f}, {0.233f, 0.279f}}
    );

    private final int largeurCarte;
    private final int largeurJeton;
    private final int hauteurJeton;
    private final int largeur;
    private final int hauteur;
    private final int nombreLignes;
    private final int nombreColonnes;
    private final int nombreLignesJetons;
    private final int nombreColonnesJetons;
    private final Map<CoordonneeCase, Maillage> casesParIndex;
    private final Map<Maillage, CoordonneeCase> indexParJetons;

    public Quadrillage(Fenetre parent, int[] tailleCadrillage, int[] tailleJetons, float[] translation, Map<String, Path> textures) throws LwjglException {
        super(parent,
                "cadrillage",
                -1,
                verifierDimensions(tailleCadrillage, tailleJetons, translation),
                new float[]{1f, 1f, 1f},
                new float[]{0f, 0f, 0f},
                textures);
        this.largeurCarte = tailleCadrillage[2];
        this.largeurJeton = tailleJetons[2];
        this.hauteurJeton = tailleJetons[3];
        this.nombreLignesJetons = tailleJetons[0];
        this.nombreColonnesJetons = tailleJetons[1];
        this.largeur = Math.addExact(largeurCarte, Math.multiplyExact(tailleJetons[1], largeurJeton));
        this.hauteur = Math.max(tailleCadrillage[3], Math.multiplyExact(tailleJetons[0], hauteurJeton));
        this.nombreLignes = tailleCadrillage[0];
        this.nombreColonnes = tailleCadrillage[1];
        int largeurCase = largeurCarte / tailleCadrillage[1];
        int hauteurCase = tailleCadrillage[3] / nombreLignes;
        this.casesParIndex = Flux.fluxPairesEntiers(tailleCadrillage[1], nombreLignes)
                .collect(Collectors.toMap(
                        index -> new CoordonneeCase(nombreLignes - index[1] - 1, index[0]),
                        index -> new Quadrilatere(this,
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
                                })));
        ajouter(GROUPE_CASES, this.casesParIndex.values());
        this.indexParJetons = new HashMap<>();
    }

    private static String genererGroupeJetons(Integer ligneCase, Integer colonneCase) {
        return MessageFormat.format("{0}-{1}-{2}", GROUPE_JETONS, ligneCase, colonneCase);
    }

    private static float[] verifierDimensions(int[] tailleCadrillage, int[] tailleJetons, float[] translation) {
        if (tailleCadrillage == null || tailleCadrillage.length < 4
                || tailleJetons == null || tailleJetons.length < 4) {
            throw new IllegalArgumentException("Les dimensions du quadrillage et des jetons doivent contenir quatre valeurs.");
        }
        if (Arrays.stream(tailleCadrillage).limit(4).anyMatch(dimension -> dimension <= 0)
                || Arrays.stream(tailleJetons).limit(4).anyMatch(dimension -> dimension <= 0)) {
            throw new IllegalArgumentException("Les dimensions du quadrillage et des jetons doivent être positives.");
        }
        if (tailleCadrillage[2] < tailleCadrillage[1] || tailleCadrillage[3] < tailleCadrillage[0]) {
            throw new IllegalArgumentException("Chaque case du quadrillage doit avoir une largeur et une hauteur positives.");
        }
        long largeurTotale = tailleCadrillage[2] + (long) tailleJetons[1] * tailleJetons[2];
        long hauteurTotale = (long) tailleJetons[0] * tailleJetons[3];
        if (largeurTotale > Integer.MAX_VALUE || hauteurTotale > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Les dimensions calculées du quadrillage dépassent les limites autorisées.");
        }
        return translation;
    }

    public void placer(Integer ligneCase, Integer colonneCase) {
        verifierCoordonneesCase(ligneCase, colonneCase);
        placer(genererGroupeJetons(ligneCase, colonneCase), ligneCase, colonneCase);
    }

    private void placer(String groupe, Integer ligneCase, Integer colonneCase) {
        Optional.ofNullable(groupes.get(groupe)).ifPresent(_ -> {
            long nombreFormes = fluxFormesAffichables(groupe).count();
            if (nombreFormes > 0) {
                Float[][] centres = COORDONNEES_CENTRES_GRAVITES_JETONS.get(Math.min(COORDONNEES_CENTRES_GRAVITES_JETONS.size(), nombreFormes));
                AtomicInteger index = new AtomicInteger(0);
                fluxFormesAffichables(groupe)
                        .limit(centres.length)
                        .forEach(forme -> {
                            Float[] coordonnees = centres[index.getAndIncrement()];
                            forme.deplacer(new float[]{colonneCase + coordonnees[0], nombreLignes - 1 - ligneCase + coordonnees[1], 0f}, 1_000);
                        });
            }
            fluxFormesInvisibles(groupe).forEach(forme -> forme.deplacer(new float[]{colonneCase + 0.5f, nombreLignes - 1 - ligneCase + 0.5f, 0f}, 1_000));
        });
    }

    private Stream<Maillage> fluxFormesAffichables(String groupe) {
        return groupes.getOrDefault(groupe, Collections.emptyList()).stream().filter(Maillage::visible);
    }

    private Stream<Maillage> fluxFormesInvisibles(String groupe) {
        return groupes.getOrDefault(groupe, Collections.emptyList()).stream().filter(forme -> !forme.visible());
    }

    public Maillage ajouterJeton(Integer ligneCase, Integer colonneCase, int ligneJeton, int colonneJeton) {
        verifierCoordonneesCase(ligneCase, colonneCase);
        if (ligneJeton < 0 || colonneJeton < 0 || ligneJeton >= tailleJetonsLignes() || colonneJeton >= tailleJetonsColonnes()) {
            throw new IndexOutOfBoundsException("Les coordonnées du jeton à afficher sont hors limites.");
        }
        Quadrilatere jeton = new Quadrilatere(this,
                "jeton",
                (nombreLignes - ligneCase - 1f) + 0.25f,
                colonneCase + 0.25f,
                nombreLignes - ligneCase - 1f,
                colonneCase,
                0f,
                new float[]{
                        (float) (largeurCarte + (colonneJeton + 1) * largeurJeton) / largeur, (float) (ligneJeton * hauteurJeton) / hauteur,
                        (float) (largeurCarte + colonneJeton * largeurJeton) / largeur, (float) (ligneJeton * hauteurJeton) / hauteur,
                        (float) (largeurCarte + colonneJeton * largeurJeton) / largeur, (float) ((ligneJeton + 1) * hauteurJeton) / hauteur,
                        (float) (largeurCarte + (colonneJeton + 1) * largeurJeton) / largeur, (float) ((ligneJeton + 1) * hauteurJeton) / hauteur
                });
        jeton.apparaitre();
        String groupe = genererGroupeJetons(ligneCase, colonneCase);
        ajouter(groupe, jeton);
        placer(groupe, ligneCase, colonneCase);
        indexParJetons.put(jeton, new CoordonneeCase(ligneCase, colonneCase));
        return jeton;
    }

    public void desactiverJetons() {
        indexParJetons.keySet().forEach(Maillage::desactiver);
    }

    public void deplacer(Maillage jeton, Integer ligneCase, Integer colonneCase) {
        Objects.requireNonNull(jeton, "Le jeton ne peut pas être null.");
        verifierCoordonneesCase(ligneCase, colonneCase);
        CoordonneeCase index = indexParJetons.get(jeton);
        if (index == null) {
            throw new IllegalArgumentException("Le jeton n'appartient pas à ce quadrillage.");
        }

        String groupeSource = genererGroupeJetons(index.ligne(), index.colonne());
        supprimer(groupeSource, jeton);
        placer(groupeSource, index.ligne(), index.colonne());

        String groupeCible = genererGroupeJetons(ligneCase, colonneCase);
        ajouter(groupeCible, jeton);
        placer(groupeCible, ligneCase, colonneCase);

        indexParJetons.put(jeton, new CoordonneeCase(ligneCase, colonneCase));
    }

    public Map<CoordonneeCase, Maillage> casesParIndex() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(casesParIndex));
    }

    private void verifierCoordonneesCase(Integer ligne, Integer colonne) {
        Objects.requireNonNull(ligne, "La ligne ne peut pas être null.");
        Objects.requireNonNull(colonne, "La colonne ne peut pas être null.");
        if (ligne < 0 || ligne >= nombreLignes || colonne < 0 || colonne >= nombreColonnes) {
            throw new IndexOutOfBoundsException("Les coordonnées de la case sont hors limites.");
        }
    }

    private int tailleJetonsLignes() {
        return nombreLignesJetons;
    }

    private int tailleJetonsColonnes() {
        return nombreColonnesJetons;
    }

    @Override
    public void apparaitre() {
        super.apparaitre();
        groupes.get(GROUPE_CASES).forEach(Maillage::apparaitre);
    }

    @Override
    public void disparaitre() {
        super.disparaitre();
        groupes.keySet().forEach(groupe -> groupes.get(groupe).forEach(Maillage::disparaitre));
    }

    public record CoordonneeCase(int ligne, int colonne) {
    }
}
