package org.mgd.lwjgl.affichage.tetehaute.composant;

public abstract class Action<T> extends Entite {
    protected final T objet;
    protected final boolean anime;
    protected boolean active;

    protected Action(T objet, boolean anime) {
        super();
        this.objet = objet;
        this.anime = anime;
    }

    protected Action(T objet, int largeur, int hauteur, boolean anime) {
        super(largeur, hauteur);
        this.objet = objet;
        this.anime = anime;
    }

    public void activer() {
        this.active = true;
    }

    public void desactiver() {
        this.active = false;
    }

    public T objet() {
        return objet;
    }

    public boolean anime() {
        return anime;
    }

    public boolean active() {
        return active;
    }
}
