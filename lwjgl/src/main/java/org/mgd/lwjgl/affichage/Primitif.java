package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;

public abstract class Primitif {
    protected final Fenetre parent;
    protected boolean visible;

    protected Primitif(Fenetre parent) {
        this.parent = parent;
    }

    public void basculer() {
        visible = !visible;
    }

    public void apparaitre() {
        visible = true;
    }
}
