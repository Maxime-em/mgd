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

    public void suivant() {
        page = Math.min(page + 1, total);
    }

    public void precedent() {
        page = Math.max(page - 1, 0);
    }

    public void calculer(long quantite) {
        total = Math.toIntExact(quantite / taille + (quantite % taille > 0 ? 1 : 0));
        if (page >= total) {
            page = total - 1;
        }
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
}
