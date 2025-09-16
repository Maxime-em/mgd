package org.mgd.lwjgl.affichage;

import org.mgd.lwjgl.Fenetre;

public abstract class ObjetAffichable {
    protected final Fenetre parent;
    protected final int priorite;
    protected boolean visible;

    protected ObjetAffichable(Fenetre parent, int priorite) {
        this.parent = parent;
        this.priorite = priorite;
    }

    public void apparaitre() {
        visible = true;
    }

    public void disparaitre() {
        visible = false;
    }

    public int priorite() {
        return priorite;
    }
}
