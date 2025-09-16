package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;

public abstract class Primitif {
    protected final Fenetre parent;
    protected final boolean apparaitreParDefaut;
    protected boolean visible;

    protected Primitif(Fenetre parent, boolean apparaitreParDefaut) {
        this.parent = parent;
        this.apparaitreParDefaut = apparaitreParDefaut;
    }

    public void apparaitre() {
        visible = true;
    }

    public void disparaitre() {
        visible = false;
    }

    public boolean apparaitreParDefaut() {
        return apparaitreParDefaut;
    }
}
