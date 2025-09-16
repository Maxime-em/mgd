package org.mgd.lwjgl.affichage;

public abstract class Primitif {
    protected boolean visible;

    public void basculer() {
        visible = !visible;
    }

    public void apparaitre() {
        visible = true;
    }
}
