package org.mgd.lwjgl.element;

public abstract class Primitif {
    protected boolean visible;

    public void basculer() {
        visible = !visible;
    }

    public void apparaitre() {
        visible = true;
    }

    public void disparaitre() {
        visible = false;
    }
}
