package org.mgd.lwjgl.affichage.element.forme;

import org.mgd.commun.Matrice;

import java.util.Arrays;
import java.util.Collections;

public class Boite {
    private final Float[] minimun;
    private final Float[] maximun;

    public Boite(Matrice<Float> matrice) {
        Matrice<Float> reduction = matrice.reduireParLigne(ligne -> Collections.min(Arrays.asList(ligne)), ligne -> Collections.max(Arrays.asList(ligne)));
        this.minimun = reduction.colonne(0);
        this.maximun = reduction.colonne(1);
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
