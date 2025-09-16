package org.mgd.lwjgl.affichage.tetehaute;

import java.util.Optional;

public final class Pagination {
    private final int taille;
    private int page;
    private int total;
    private boolean aChange;

    public Pagination(int taille, int page, int total) {
        this.page = page;
        this.taille = taille;
        this.total = total;
        this.aChange = true;
    }

    public void initialiser() {
        page = 0;
        aChange = false;
    }

    public void suivant() {
        page = Math.min(page + 1, total);
        aChange = true;
    }

    public void precedent() {
        page = Math.max(page - 1, 0);
        aChange = true;
    }

    public void seRendre(int index) {
        page = index / taille;
        aChange = true;
    }

    public void calculer(long quantite) {
        total = Math.max(Math.toIntExact(quantite / taille + (quantite % taille > 0 ? 1 : 0)), 1);
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

    public Optional<Boolean> aChange() {
        if (aChange) {
            aChange = false;
            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }
}
