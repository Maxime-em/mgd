package org.mgd.lwjgl.affichage.tetehaute;

public final class Pagination {
    private final int taille;
    private int page;
    private int total;

    public Pagination(int taille, int page, int total) {
        this.page = page;
        this.taille = taille;
        this.total = total;
    }

    public int taille() {
        return taille;
    }

    public int page() {
        return page;
    }

    public int total() {
        return total;
    }

    public void suivant() {
        page = Math.min(page + 1, total);
    }

    public void precedent() {
        page = Math.max(page - 1, 0);
    }

    public void total(int total) {
        this.total = total;
    }
}
