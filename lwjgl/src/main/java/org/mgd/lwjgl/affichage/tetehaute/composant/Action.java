package org.mgd.lwjgl.affichage.tetehaute.composant;

public abstract class Action<T> extends Entite {
    protected final T objet;

    protected Action(T objet) {
        super();
        this.objet = objet;
    }

    protected Action(T objet, int largeur, int hauteur) {
        super(largeur, hauteur);
        this.objet = objet;
    }

    public T objet() {
        return objet;
    }
}
