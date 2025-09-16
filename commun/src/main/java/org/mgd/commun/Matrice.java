package org.mgd.commun;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.BinaryOperator;
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

    public static Matrice<Float> projection60() {
        return Matrice.parValeurs(4,
                4,
                ConstantesMathematiques.RACINE_TROIS * 9f / 16f, 0f, 0f, 0f,
                0f, ConstantesMathematiques.RACINE_TROIS, 0f, 0f,
                0f, 0f, -1f, -2f,
                0f, 0f, -1f, 0f);
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

    public void insererParLignes(MatriceInsertion<T> insertion) {
        parcoursParLignes((ligne, colonne, index, valeur) -> valeurs[ligne][colonne] = insertion.obtenir(ligne, colonne, index));
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

    @SuppressWarnings("unchecked")
    public T[] appliquer(T[] vecteur) {
        if (vecteur.length < nombreColonnes) {
            throw new IllegalArgumentException(MessageFormat.format("La taille du vecteur doit être d''au moins {0}", nombreColonnes));
        }
        T[] resultat = (T[]) Array.newInstance(classe, nombreLignes);
        IntStream.range(0, nombreLignes).forEach(ligne -> resultat[ligne] = neutre);
        parcoursParLignes((ligne, colonne, index, valeur) -> resultat[ligne] = somme.apply(resultat[ligne], multiplication.apply(vecteur[colonne], valeur)));
        return resultat;
    }

    public T valeur(int ligne, int colonne) {
        return valeurs[ligne][colonne];
    }
}
