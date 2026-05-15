package org.mgd.commun;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Pour les matrices confère opengl.mac
 *
 * @param <T>
 */
public class Matrice<T> {
    private final Class<T> classe;
    private final int nombreLignes;
    private final int nombreColonnes;
    private final T neutre;
    private final T[][] valeurs;
    private final BinaryOperator<T> multiplication;
    private final BinaryOperator<T> somme;

    @SuppressWarnings("unchecked")
    private Matrice(Class<T> classe, int nombreLignes, int nombreColonnes, T neutre, BinaryOperator<T> multiplication, BinaryOperator<T> somme) {
        this.classe = classe;
        this.nombreLignes = nombreLignes;
        this.nombreColonnes = nombreColonnes;
        this.neutre = neutre;
        this.multiplication = multiplication;
        this.somme = somme;
        this.valeurs = (T[][]) Array.newInstance(classe, nombreLignes, nombreColonnes);
    }

    public static <T> Matrice<T> identite(Class<T> classe, int nombreLignes, int nombreColonnes, T unite, T neutre, BinaryOperator<T> multiplication, BinaryOperator<T> somme) {
        Matrice<T> matrice = new Matrice<>(classe, nombreLignes, nombreColonnes, neutre, multiplication, somme);
        matrice.insererParLignes((ligne, colonne, index) -> Objects.equals(ligne, colonne) ? unite : neutre);
        return matrice;
    }

    @SafeVarargs
    public static <T> Matrice<T> parValeurs(Class<T> classe, int nombreLignes, int nombreColonnes, T neutre, BinaryOperator<T> multiplication, BinaryOperator<T> somme, T... valeurs) {
        Matrice<T> matrice = new Matrice<>(classe, nombreLignes, nombreColonnes, neutre, multiplication, somme);
        matrice.insererParLignes((ligne, colonne, index) -> index < valeurs.length ? valeurs[index] : neutre);
        return matrice;
    }

    public static Matrice<Float> identitef(int nombreLignes, int nombreColonnes) {
        return Matrice.identite(Float.class, nombreLignes, nombreColonnes, 1f, 0f, (e1, e2) -> e1 * e2, Float::sum);
    }

    public static Matrice<Float> parValeurs(int nombreLignes, int nombreColonnes, Float... valeurs) {
        return Matrice.parValeurs(Float.class, nombreLignes, nombreColonnes, 0f, (e1, e2) -> e1 * e2, Float::sum, valeurs);
    }

    public static Matrice<Float> transformation(float[] translation, float[] agrandissement, float[] rotation) {
        float costhx = (float) Math.cos(rotation[0]);
        float costhy = (float) Math.cos(rotation[1]);
        float costhz = (float) Math.cos(rotation[2]);
        float sinthx = (float) Math.sin(rotation[0]);
        float sinthy = (float) Math.sin(rotation[1]);
        float sinthz = (float) Math.sin(rotation[2]);
        return Matrice.parValeurs(4,
                4,
                agrandissement[0] * costhy * costhz, -agrandissement[0] * costhy * sinthz, agrandissement[0] * sinthy, translation[0],
                agrandissement[1] * (costhx * sinthz + sinthx * sinthy * costhz), -agrandissement[1] * (sinthx * sinthy * sinthz - costhx * costhz), -agrandissement[1] * sinthx * costhy, translation[1],
                agrandissement[2] * (sinthx * sinthz - costhx * sinthy * costhz), agrandissement[2] * (costhx * sinthy * sinthz + sinthx * costhz), agrandissement[2] * costhx * costhy, translation[2],
                0f, 0f, 0f, 1f);
    }

    public static Matrice<Float> transformation(float[] translation, float[] agrandissement) {
        return Matrice.parValeurs(4,
                4,
                1f,
                agrandissement[0], 0f, 0f, translation[0],
                0f, agrandissement[1], 0f, translation[1],
                0f, 0f, agrandissement[2], translation[2],
                0f, 0f, 0f, 1f);
    }

    public void parcoursParLignes(MatriceIteration<T> traitement) {
        IntStream.range(0, nombreLignes)
                .forEach(ligne -> IntStream.range(0, nombreColonnes)
                        .forEach(colonne -> traitement.recevoir(ligne, colonne, ligne * nombreColonnes + colonne, valeurs[ligne][colonne])));
    }

    public void parcoursParColonnes(MatriceIteration<T> traitement) {
        IntStream.range(0, nombreColonnes)
                .forEach(colonne -> IntStream.range(0, nombreLignes)
                        .forEach(ligne -> traitement.recevoir(colonne, ligne, colonne * nombreColonnes + ligne, valeurs[ligne][colonne])));
    }

    public Matrice<T> insererParLignes(MatriceInsertion<T> insertion) {
        parcoursParLignes((ligne, colonne, index, _) -> valeurs[ligne][colonne] = insertion.obtenir(ligne, colonne, index));
        return this;
    }

    public void modifierValeur(int ligne, int colonne, T element, BinaryOperator<T> modifier) {
        valeurs[ligne][colonne] = modifier.apply(valeurs[ligne][colonne], element);
    }

    public int nombreLignes() {
        return nombreLignes;
    }

    public int nombreColonnes() {
        return nombreColonnes;
    }

    public Matrice<T> multiplication(Matrice<T> matrice) {
        if (nombreColonnes != matrice.nombreLignes) {
            throw new IllegalArgumentException("Les tailles des matrices ne sont pas compatibles");
        }
        Matrice<T> nouvelle = new Matrice<>(classe, nombreLignes, matrice.nombreColonnes, neutre, multiplication, somme);
        IntStream.range(0, nombreLignes).forEach(ligne -> IntStream.range(0, matrice.nombreColonnes).forEach(colonne ->
                nouvelle.valeurs[ligne][colonne] = IntStream.range(0, nombreColonnes)
                        .mapToObj(rang -> multiplication.apply(valeurs[ligne][rang], matrice.valeurs[rang][colonne]))
                        .reduce(somme)
                        .orElse(neutre)));
        return nouvelle;
    }

    public T valeur(int ligne, int colonne) {
        return valeurs[ligne][colonne];
    }

    @SuppressWarnings("unchecked")
    public T[] colonne(int index) {
        return IntStream.range(0, nombreLignes).mapToObj(ligne -> valeurs[ligne][index]).toArray(taille -> (T[]) Array.newInstance(classe, taille));
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final Matrice<T> reduireParLigne(Function<T[], T>... operations) {
        Matrice<T> nouvelle = new Matrice<>(classe, nombreLignes, operations.length, neutre, multiplication, somme);
        IntStream.range(0, nombreLignes).forEach(ligne -> nouvelle.valeurs[ligne] = Arrays.stream(operations).map(operation -> operation.apply(valeurs[ligne])).toArray(taille -> (T[]) Array.newInstance(classe, taille)));
        return nouvelle;
    }
}
