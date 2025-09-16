package org.mgd.lwjgl.affichage;

public abstract class Transition<O, T> {
    private final O objet;
    private final T depart;
    private final T arrive;
    private final long duree;
    private long temps;

    protected Transition(O objet, T depart, T arrive, long duree) {
        this.objet = objet;
        this.depart = depart;
        this.arrive = arrive;
        this.duree = duree;
    }

    protected abstract T multiplierParScalaire(Double scalaire, T valeur);

    protected abstract T sommer(T valeur1, T valeur2);

    protected abstract void appliquer(O objet, T valeur);

    public boolean finie() {
        return temps >= duree;
    }

    public void lineariser(long ellipse) {
        temps += ellipse;
        if (temps <= 0) {
            appliquer(objet, depart);
        } else if (temps >= duree) {
            appliquer(objet, arrive);
        } else {
            lineariserPourmille(temps * 1_000 / duree);
        }
    }

    private void lineariserPourmille(long pourmille) {
        appliquer(objet, sommer(multiplierParScalaire((double) (1_000 - pourmille) / 1_000, depart), multiplierParScalaire((double) pourmille / 1_000, arrive)));
    }

    public T arrive() {
        return arrive;
    }
}
