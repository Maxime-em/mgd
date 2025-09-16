package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;

public abstract class Primitif {
    protected final Fenetre parent;
    protected boolean visible;

    protected Primitif(Fenetre parent) {
        this.parent = parent;
    }

    public void apparaitre() {
        visible = true;
    }

    public void disparaitre() {
        visible = false;
    }
}
