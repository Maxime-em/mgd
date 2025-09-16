package org.mgd.lwjgl.forme;

import org.mgd.commun.Matrice;

public class Boite {
    private final Float[] minimun;
    private final Float[] maximun;

    public Boite(float[] valeurs) {
        this.minimun = new Float[]{valeurs[0], valeurs[1], valeurs[2], 1f};
        this.maximun = new Float[]{valeurs[0], valeurs[1], valeurs[2], 1f};
        int index = 3;
        do {
            float position = valeurs[index];
            int coordonnee = index % 3;
            if (position < this.minimun[coordonnee]) {
                this.minimun[coordonnee] = position;
            } else if (position > this.maximun[coordonnee]) {
                this.maximun[coordonnee] = position;
            }
            index++;
        } while (index < valeurs.length);
    }

    public Boite transformer(Matrice<Float> matrice) {
        Float[] minimunCalcule = matrice.appliquer(this.minimun);
        Float[] maximunCalcule = matrice.appliquer(this.maximun);
        float[] valeurs = new float[6];
        for (int i = 0; i < 3; i++) {
            valeurs[i] = minimunCalcule[i];
            valeurs[i + 3] = maximunCalcule[i];
        }
        return new Boite(valeurs);
    }

    public float minimumx() {
        return minimun[0];
    }

    public float minimumy() {
        return minimun[1];
    }

    public float minimumz() {
        return minimun[2];
    }

    public float maximunx() {
        return maximun[0];
    }

    public float maximuny() {
        return maximun[1];
    }
}
